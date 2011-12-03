function BoardCanvas(canvasElt, cellSize) {
  this.width = canvasElt.offsetWidth / cellSize;
  this.height = canvasElt.offsetHeight / cellSize;
  this.board = new Board(this.width, this.height);
  var canvas = new Canvas(canvasElt, this.board, cellSize);
  this.canvas = canvas;
  addEvent(canvasElt, 'click', func(canvas, canvas.clickHandler));
};
