'use strict';

angular.module('docs').controller('ModalFileEditImage', function ($scope, $uibModalInstance, file) {
  $scope.file = file;

  let canvas = null;
  let brightnessFilter = new fabric.Image.filters.Brightness({ brightness: 0 });
  let croppingRect = null;
  let cropping = false;

  setTimeout(() => {
    canvas = new fabric.Canvas('canvas', {
      isDrawingMode: false,
      backgroundColor: 'white'
    });

    const url = '../api/file/' + file.id + '/data';
    fabric.Image.fromURL(url, function (img) {
      const scaleFactor = Math.min(600 / img.width, 400 / img.height);

      const scaledWidth = img.width * scaleFactor;
      const scaledHeight = img.height * scaleFactor;

      canvas.setWidth(scaledWidth);
      canvas.setHeight(scaledHeight);

      img.set({
        originX: 'center',
        originY: 'center',
        left: scaledWidth / 2,
        top: scaledHeight / 2,
        scaleX: scaleFactor,
        scaleY: scaleFactor,
        selectable: false
      });

      img.filters.push(brightnessFilter);
      img.applyFilters();

      canvas.setBackgroundImage(img, canvas.renderAll.bind(canvas));
    });
  }, 0);

  $scope.addRect = function () {
    const rect = new fabric.Rect({
      left: 50,
      top: 50,
      fill: 'rgba(255,0,0,0.3)',
      width: 100,
      height: 50,
      stroke: 'red',
      strokeWidth: 2
    });
    canvas.add(rect);
  };

  $scope.addLine = function () {
    const line = new fabric.Line([50, 100, 200, 100], {
      left: 50,
      top: 100,
      stroke: 'blue',
      strokeWidth: 3
    });
    canvas.add(line);
  };

  $scope.enableDraw = function () {
    canvas.isDrawingMode = true;
    canvas.freeDrawingBrush = new fabric.PencilBrush(canvas);
    canvas.freeDrawingBrush.width = 3;
    canvas.freeDrawingBrush.color = 'black';
  };

  $scope.disableDraw = function () {
    canvas.isDrawingMode = false;
  };

  $scope.clearCanvas = function () {
    canvas.getObjects().forEach(obj => {
      if (obj !== canvas.backgroundImage) canvas.remove(obj);
    });
  };

  $scope.adjustExposure = function (delta) {
    brightnessFilter.brightness += delta;
    if (canvas.backgroundImage) {
      canvas.backgroundImage.applyFilters();
      canvas.renderAll();
    }
  };

  $scope.rotate = function (angle) {
    if (canvas.backgroundImage) {
      canvas.backgroundImage.angle += angle;
      canvas.renderAll();
    }
  };

  $scope.startCrop = function () {
    cropping = true;

    croppingRect = new fabric.Rect({
      left: 50,
      top: 50,
      width: 200,
      height: 150,
      fill: 'rgba(0,255,0,0.2)',
      stroke: 'green',
      strokeWidth: 2,
      selectable: true,
      hasBorders: true,
      hasControls: true
    });

    canvas.add(croppingRect);
    canvas.setActiveObject(croppingRect);
  };

  $scope.applyEdits = function () {
    canvas.discardActiveObject().renderAll();

    if (cropping && croppingRect) {
      const crop = croppingRect.getBoundingRect();
      const croppedCanvas = document.createElement('canvas');
      const ctx = croppedCanvas.getContext('2d');

      croppedCanvas.width = crop.width;
      croppedCanvas.height = crop.height;

      const originalCanvasEl = canvas.getElement();
      ctx.drawImage(
        originalCanvasEl,
        crop.left, crop.top,
        crop.width, crop.height,
        0, 0,
        crop.width, crop.height
      );

      croppedCanvas.toBlob(function (blob) {
        if (!blob) {
          alert('Failed to crop image.');
          return;
        }

        $uibModalInstance.close(blob);
      }, file.mimetype);

    } else {
      const canvasEl = canvas.getElement();
      const mimeType = file.mimetype.startsWith('image/') ? file.mimetype : 'image/png';

      canvasEl.toBlob(function (blob) {
        if (!blob) {
          alert('Failed to generate image blob.');
          return;
        }

        $uibModalInstance.close(blob);
      }, mimeType);
    }
  };

  $scope.cancel = function () {
    $uibModalInstance.dismiss('cancel');
  };
});
