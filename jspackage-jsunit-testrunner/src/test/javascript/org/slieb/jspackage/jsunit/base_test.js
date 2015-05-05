//goog.require('goog.testing.jsunit');


//goog.exportSymbol("testBase", function () {

    var baseCtorCalled = false;
    var extendedCtorCalled = false;

    /**
     * @constructor
     */
    function Base() {
    };

    /**
     * @constructor
     * @extends {Base}
     */
    function Extended() {
        goog.base(this);
    };
    goog.inherits(Extended, Base);


    var extended = new Extended();

    assertTrue(baseCtorCalled);
    assertTrue(extendedCtorCalled);
//});