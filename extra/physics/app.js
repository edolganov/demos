
$(function(){
	new App().init();
});

function App(){

	var canvas = $("#canvas");
	var c = canvas[0].getContext('2d');
	var w = canvas.width();
	var h = canvas.height();
	var intervalId;
	var delta = 15;
	var x, y;
	var prevX, prevY;
	var curX, curY;
	var ob = new Obj(w/2, h/2-100, 30, 150);


	this.init = function(){

		//intervalId = setInterval(updateWorld, 100);
		updateWorld();

	};

	function updateWorld(){

		c.fillRect(0, 0, w, h);

		c.strokeStyle = 'white';
		c.lineWidth="1";

		prevY = delta;
		for(y = delta; y <= h; y += delta){
			
			curY = y;
			prevX = 0;

			for(x = delta; x <= w; x += delta){

				curX = x;
				curY = y;

				c.beginPath();
				c.moveTo(prevX, prevY);
				c.lineTo(curX, curY);
				c.stroke();

				prevX = curX;
				prevY = curY;
			}
		}

		for(x = delta; x <= w; x += delta){
			for(y = delta; y <= h; y += delta){
				c.beginPath();
				c.moveTo(x, y - delta);
				c.lineTo(x, y);
				c.stroke();
			}
		}

		c.fillStyle = 'blue';
		c.beginPath();
		c.arc(ob.x, ob.y, ob.r, 0, 360);
		c.fill();
		c.stroke();

	}

}


function Obj(x, y, radius, mass){
	this.x = x;
	this.y = y;
	this.r = radius;
	this.m = mass;
}

