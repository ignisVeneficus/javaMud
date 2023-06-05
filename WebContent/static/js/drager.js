(function ( $ ) {
	jQuery.fn.extend({
		slider: function(options) { 
			var el = $(this); 
			this.config = options;
			if (this.length > 1) {
				this.each(function() { $(this).slider(options) });
				return this;
			}
			console.debug("created ball");
			this.ball=$("<div></div>").addClass(this.config.cssClass);
			this.ball.appendTo(this);
			this.pressDown = false;

			let width = parseFloat(this.css('width'));
			this.ballwidth = parseFloat(this.ball.css('width'))+ parseFloat(this.ball.css("border-left-width"))*2;
			let itemOffset = (width - this.ballwidth)/2;
			this.ball.css({
				left: `${itemOffset}px`,
				top: `-${this.ballwidth/2}px`,
				zIndex: 3,
				position: 'relative'
			});
	
			function moveBall(obj, percent){
				percent < 0 && (percent = 0);
				percent > 1 && (percent = 1);
				percent = 1 - percent;
				obj.ball.offset({top: obj.offset().top + percent * parseFloat(obj.css('height'))-obj.ballwidth/2});
				if(obj.config.tooltip){
					obj.ball.attr("data-tooltip",""+obj.config.tooltip + (Math.floor((1-percent)*100)) + "%");
				}
			}
			
			function getPercent(obj,event) {
				let offsetX = obj.offset().top;
				let total = parseFloat(obj.css('height'));
				let progress = (event.pageY-offsetX)/total;
				progress < 0 && (progress = 0);
				progress > 1 && (progress = 1);
				return 1-progress;
			}
			function off(obj,event){
				obj.pressDown = false;
				let progress = getPercent(obj,event);
				obj.config.valueChanged && obj.config.valueChanged(progress);
				console.debug(progress);
			}
			this.ball.on({
				 mouseup: event => {
					event.stopPropagation();
					off(this,event);
				},
				mousedown: event => {
					event.stopPropagation();
					this.pressDown = true;
				},
				mousemove: event => {
					if(!this.pressDown) return;
					let progress = getPercent(this,event);
					moveBall(this,progress);
				}
			});
			
			$('body').on({
				mouseup: () => {
					if (!this.pressDown) return;
					off(this,event);
				},
				mousemove: event => {
					if(!this.pressDown) return;
					let progress = getPercent(this,event);
					moveBall(this,progress);
				}
			});
		}
	});
}( jQuery ));