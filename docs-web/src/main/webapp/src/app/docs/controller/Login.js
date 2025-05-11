'use strict';

/**
 * Login controller.
 */
angular.module('docs').controller('Login', function(Restangular, $scope, $rootScope, $state, $stateParams, $dialog, User, $translate, $uibModal) {
  $scope.codeRequired = false;

  // Get the app configuration
  Restangular.one('app').get().then(function(data) {
    $rootScope.app = data;
  });

  // Login as guest
  $scope.loginAsGuest = function() {
    $scope.user = {
      username: 'guest',
      password: ''
    };
    $scope.login();
  };
  
  // Login
  $scope.login = function() {
    User.login($scope.user).then(function() {
      User.userInfo(true).then(function(data) {
        $rootScope.userInfo = data;
      });

      if($stateParams.redirectState !== undefined && $stateParams.redirectParams !== undefined) {
        $state.go($stateParams.redirectState, JSON.parse($stateParams.redirectParams))
          .catch(function() {
            $state.go('document.default');
          });
      } else {
        $state.go('document.default');
      }
    }, function(data) {
      if (data.data.type === 'ValidationCodeRequired') {
        // A TOTP validation code is required to login
        $scope.codeRequired = true;
      } else {
        // Login truly failed
        var title = $translate.instant('login.login_failed_title');
        var msg = $translate.instant('login.login_failed_message');
        var btns = [{result: 'ok', label: $translate.instant('ok'), cssClass: 'btn-primary'}];
        $dialog.messageBox(title, msg, btns);
      }
    });
  };

  // Password lost
  $scope.openPasswordLost = function () {
    $uibModal.open({
      templateUrl: 'partial/docs/passwordlost.html',
      controller: 'ModalPasswordLost'
    }).result.then(function (username) {
      if (username === null) {
        return;
      }

      // Send a password lost email
      Restangular.one('user').post('password_lost', {
        username: username
      }).then(function () {
        var title = $translate.instant('login.password_lost_sent_title');
        var msg = $translate.instant('login.password_lost_sent_message', { username: username });
        var btns = [{result: 'ok', label: $translate.instant('ok'), cssClass: 'btn-primary'}];
        $dialog.messageBox(title, msg, btns);
      }, function () {
        var title = $translate.instant('login.password_lost_error_title');
        var msg = $translate.instant('login.password_lost_error_message');
        var btns = [{result: 'ok', label: $translate.instant('ok'), cssClass: 'btn-primary'}];
        $dialog.messageBox(title, msg, btns);
      });
    });
  };

  // ✅ 注册账号请求模态框
  $scope.openRegisterRequest = function () {
    $uibModal.open({
      templateUrl: 'partial/docs/registerRequestModal.html',
      controller: function ($scope, $uibModalInstance, Restangular) {
        $scope.form = {};

        $scope.cancel = function () {
          $uibModalInstance.dismiss('cancel');
        };

        $scope.submit = function () {
          if (!$scope.form.username || !$scope.form.email) {
            $dialog.messageBox('提示', '请填写完整信息', [{ result: 'ok', label: '确定', cssClass: 'btn-primary' }]);
            return;
          }

          Restangular.all('user-request').post($scope.form).then(() => {
            $uibModalInstance.close();
            $dialog.messageBox('成功', '申请已提交，管理员审核后您将收到通知。', [{ result: 'ok', label: '确定', cssClass: 'btn-primary' }]);
          }, (error) => {
            const errMsg = error.data && error.data.message ? error.data.message : '申请提交失败，请稍后再试。';
            $dialog.messageBox('错误', errMsg, [{ result: 'ok', label: '确定', cssClass: 'btn-primary' }]);
          });
        };
      },
      resolve: {
        Restangular: () => Restangular
      }
    });
  };
});