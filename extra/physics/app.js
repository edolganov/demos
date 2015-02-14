
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



	this.init = function(){

		//intervalId = setInterval(updateWorld, 100);
		updateWorld();

	};

	function updateWorld(){

		c.fillRect(0, 0, w, h);

		c.strokeStyle = 'white';
		c.lineWidth="1";

		for(y = delta; y <= h; y += delta){
			for(x = delta; x <= w; x += delta){
				c.beginPath();
				c.moveTo(x - delta, y);
				c.lineTo(x, y);
				c.stroke();
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


	}

}


function Circle(x, y, color){



}

