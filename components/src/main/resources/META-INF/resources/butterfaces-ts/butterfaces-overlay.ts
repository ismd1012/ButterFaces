///<reference path="jquery.d.ts"/>

module ButterFaces {

    export class Overlay {
        isHiding:boolean;
        delay:number;
        selector:string;
        isTransparentBlockingOverlayActive:boolean;

        constructor(delay = 500, isTransparentBlockingOverlayActive = true, selector = 'body') {
            this.isHiding = true;
            this.delay = delay;
            this.isTransparentBlockingOverlayActive = isTransparentBlockingOverlayActive;
            this.selector = selector

            console.log('ButterFaces.Overlay.constructor - creating overlay with delay is ' + this.delay + ', isTransparentBlockingOverlayActive is ' + this.isTransparentBlockingOverlayActive + ', selector is ' + this.selector);
        }

        public show() {
            let $elementsToDisable = $(this.selector);

            $elementsToDisable.each((index, elementToDisable) => {

                let $elementToDisable = $(elementToDisable);

                //if ($elementToDisable.find(".butter-component-overlay").length === 0) {
                this.isHiding = false;

                console.log("ButterFaces.Overlay.show - appending not displayed overlay to body");

                let $overlay = $('<div class="butter-component-overlay"><div class="butter-component-spinner"><div></div><div></div><div></div><div></div></div></div>');

                if (this.selector === 'body') {
                    $overlay.addClass('overlay-body');
                } else {
                    // TODO if blockpage is true set it to max size
                    $overlay.offset($elementToDisable.offset());
                    $overlay.width($elementToDisable.outerWidth());
                    $overlay.height($elementToDisable.outerHeight());
                    $overlay.addClass('overlay-body-child');
                    $overlay.css({'position': 'absolute'}); // IE overrides css position so set it here
                }

                $('body').append($overlay);

                if (this.isTransparentBlockingOverlayActive) {
                    console.log("ButterFaces.Overlay.show - isTransparentBlockingOverlayActive is true, showing transparent overlay direcly");
                    $overlay.show();
                }

                window.setTimeout(() => {
                    if (!this.isHiding && !this.isTransparentBlockingOverlayActive) {
                        console.log("ButterFaces.Overlay.show - deferred: isTransparentBlockingOverlayActive is false, showing transparent overlay after delay");
                        $overlay.show();
                    }

                    if (!this.isHiding) {
                        console.log("ButterFaces.Overlay.show - deferred: starting animation to make overlay intransparent");
                        $overlay
                            .stop(true)
                            .animate({
                                opacity: 1
                            }, 300, () => {
                                console.log("ButterFaces.Overlay.show - deferred: animation ended to make overlay intransparent");
                            });
                    }
                }, this.delay);
                //}
            });
        }

        public hide() {
            console.log("ButterFaces.Overlay.hide - starting animation to make overlay transparent");
            var $overlay = $("body .butter-component-overlay");
            this.isHiding = true;
            $overlay
                .stop(true)
                .animate({
                    opacity: 0
                }, 300, () => {
                    $overlay.remove();
                    console.log("ButterFaces.Overlay.hide - animation ended to make overlay transparent, OVERLAY REMOVED");
                });
        }
    }
}