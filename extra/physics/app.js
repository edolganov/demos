
$(function(){
	new App().init();
});

function App(){

	var canvas = $("#canvas");
	var c = canvas[0].getContext('2d');
	var w = canvas.width();
	var h = canvas.height();
	var intervalId;


	this.init = function(){

		intervalId = setInterval(updateWorld, 100);


	};

	function updateWorld(){
		c.fillRect(0, 0, w, h);
	}

}


function Circle(x, y, color){



}

