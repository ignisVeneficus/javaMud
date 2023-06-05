/**
* @license MIT
* Yet Another CSS Tooltip jQuery Plugin - Adds a CSS tooltip for the images in a jQuery selection set
* version @VERSION@
* by JM Alarcon (https://github.com/jmalarcon/)
*
*https://github.com/jmalarcon/jquery.YACSSTooltip
*/
(function($){
    $.fn.extend({
        addTooltip: function() {
            /*  This element will be the tooltip that is shown.
            Is there only one per page.
            It has a "weird" id to avoid collisions */
        	// style: display: none; position: absolute; border: 1px solid #333; background-color: #161616; border-radius: 5px; padding: 5px; color: #fff; font-size: 12px Arial;max-width:250px;line-break:auto;word-break:normal;word-break:break-word;overflow-wrap: break-word;word-spacing:0px;white-space:normal;text-align:center;overflow-wrap:normal;
        	/*
        	var idTT = 'Tooltip';
            $('<div id="' + idTT + '" class="Tooltip" ></div>').appendTo('body');
			*/
        	
            var ttShown = false;
            this.mouseover(//On hover...
                function(event) {
                	//console.debug("ON",$(this))
                	var ttText = $(this).attr("data-tooltip")
                     if (!ttText)    //If there's no text to be shown in the tooltip just don't do anything...
                        return;
                    ttShown = true;
                    $('#tooltip').html(ttText).show().data("parent",$(this));
					event.stopPropagation();
                   
                }).mouseout(
                //On mouse exit
                function(event) {
                	//console.debug("OFF",$(this))
                	ttShown = false;
                    $('#tooltip').hide();    //Hide the tooltip
					event.stopPropagation();
                }).mousemove(function (e) {//On mouse move position the tooltip next to the pointer

                    if (!ttShown) return;
                    //Get X coordinates
                    var mousex = e.pageX + 20;
                    //Get Y coordinates
                    var mousey = e.pageY + 10;
                    
                    //Check if it's inside the boundaries
                    var $tooltip = $('#tooltip'),
                        wW = $(window).scrollLeft() + $(window).width(),
                        wH = $(window).scrollTop() + $(window).height();
                     if((mousex + $tooltip.outerWidth()) > wW)
                        mousex = wW - $tooltip.outerWidth();
                    if((mousey + $tooltip.outerHeight()) > wH)
                        mousey = e.pageY - $tooltip.outerHeight() - 10;
                    //Show tooltip
                    let parent=$tooltip.data("parent");
                   
                    $tooltip.css({
                        top: mousey,
                        left: mousex
                    });
                    /*
                    $tooltip.offset({
                        top: mousey,
                        left: mousex
                    });
                    */
                    $tooltip.html(parent.attr("data-tooltip"));
                });
        return this;
        }
    });
})(jQuery);