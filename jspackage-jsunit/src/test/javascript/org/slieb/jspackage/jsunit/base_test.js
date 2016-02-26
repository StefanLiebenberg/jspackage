goog.require('goog.testing.jsunit');


goog.exportSymbol("testBase", function () {

    var baseCtorCalled = false;
    var extendedCtorCalled = false;


    /**
     * @constructor
     */
    function Base() {
        baseCtorCalled = true;
    };

    /**
     * @constructor
     * @extends {Base}
     */
    function Extended() {
        goog.base(this);
        extendedCtorCalled = true;
    };
    goog.inherits(Extended, Base);

    var extended = new Extended();

    assertTrue(baseCtorCalled);
    assertTrue(extendedCtorCalled);
});