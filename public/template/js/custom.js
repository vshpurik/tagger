/*-----------------------------------------------------------------------------------
/* Custom JavaScript
-----------------------------------------------------------------------------------*/
	  
/* ----------------- Start Document ----------------- */
jQuery(document).ready(function() {

    /*----------------------------------------------------*/
    /*	Main Navigation
/*----------------------------------------------------*/

    /* Menu */
    (function() {

        var $mainNav    = $('#navigation').children('ul');

        $mainNav.on('mouseenter', 'li', function() {
            var $this    = $(this),
            $subMenu = $this.children('ul');
            if( $subMenu.length ) $this.addClass('hover');
            $subMenu.hide().stop(true, true).slideDown('fast');
        }).on('mouseleave', 'li', function() {
            $(this).removeClass('hover').children('ul').stop(true, true).slideUp('fast');
        });
		
    })();
	
    /* Responsive Menu */
    (function() {
        selectnav('nav', {
            label: 'Menu',
            nested: true,
            indent: '-'
        });
				
    })();


    /*----------------------------------------------------*/
    /*	Image Overlay
/*----------------------------------------------------*/

    $(document).ready(function () {
        $('.picture a').hover(function () {
            $(this).find('.image-overlay-zoom, .image-overlay-link').stop().fadeTo('fast', 1);
        },function () {
            $(this).find('.image-overlay-zoom, .image-overlay-link').stop().fadeTo('fast', 0);
        });
    });


    /*----------------------------------------------------*/
    /*	Back To Top Button
/*----------------------------------------------------*/

    jQuery('#scroll-top-top a').click(function(){
        jQuery('html, body').animate({
            scrollTop:0
        }, 300); 
        return false; 
    }); 
	

    /*----------------------------------------------------*/
    /*	Fancybox
/*----------------------------------------------------*/
    (function() {

        $('[rel=image]').fancybox({
            type        : 'image',
            openEffect  : 'fade',
            closeEffect	: 'fade',
            nextEffect  : 'fade',
            prevEffect  : 'fade',
            helpers     : {
                title   : {
                    type : 'inside'
                }
            },

            /* BEGIN F5280; See ProductItem class documentation for details. */
            afterLoad : function() {
              var productItemData = this.id.split(",");
              var productItemId = productItemData[0];
              var locationId = productItemData[1];
              var portfolioProductTagId = productItemData[2];
              var moreProductTagId = productItemData[3];
              
              var title = this.title;
              this.title = '';

              this.title += '<div class="btn-group">';
              if(this.index > 0) {
                this.title += '&nbsp;<button class="btn btn-small btn-inverse" onclick="$.fancybox.prev(); return false;">Previous</button>';
              }
              // this.title += ' ' + title + ' ';
              if(this.index < this.group.length - 1) {
                this.title += '&nbsp;<button class="btn btn-small btn-inverse" onclick="$.fancybox.next(); return false;">Next</button>';
              }
              this.title += '</div>';

              if (locationId != 0) {
            	  this.title += '&nbsp;<button class="btn btn-small" onclick="f5280ViewLocation(' + locationId + '); return false;">Location</button>';
              }
              
              if (portfolioProductTagId != 0) {
            	  this.title += '&nbsp;<button class="btn btn-small" onclick="f5280ViewPortfolio(' + portfolioProductTagId + '); return false;">Portfolio</button>';
              }

              if (moreProductTagId != 0) {
            	  this.title += '&nbsp;<button class="btn btn-small" onclick="f5280ViewCategory(' + moreProductTagId + '); return false;">More</button>';
              }

              this.title += '&nbsp;<button class="btn btn-small btn-success" onclick="f5280AddToCart(' + productItemId + '); return false;">Add To Cart</button>';
              this.title += '&nbsp;<button class="btn btn-small btn-primary" onclick="f5280ViewCart(); return false;">View Cart</button>';
            }
            /* END F5280 */

        });
	
        $('[rel=image-gallery]').fancybox({
            nextEffect  : 'fade',
            prevEffect  : 'fade',
            helpers     : {
                title   : {
                    type : 'inside'
                },
                buttons  : {},
                media    : {}
            }
        });
	
	
    })();
	
        // tooltip for social media
    $('.tooltip-demo').tooltip({
      selector: "a[rel=tooltip]"
    })
    //tool tip for tool tips shortcode
    $('.tooltips').tooltip({
      selector: "a[rel=tooltip]"
    })
/* ------------------ End Document ------------------ */
});
