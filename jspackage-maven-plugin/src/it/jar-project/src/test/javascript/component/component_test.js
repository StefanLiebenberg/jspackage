goog.require('goog.testing.jsunit');
goog.require('goog.dom');

/**
 *
 */
goog.exportSymbol('test_ComponentIsOnScreen', function () {
    var component = goog.dom.getElementByClass("my-component");
    assertNotNull(component);
    assertTrue(goog.dom.isElement(element));
});