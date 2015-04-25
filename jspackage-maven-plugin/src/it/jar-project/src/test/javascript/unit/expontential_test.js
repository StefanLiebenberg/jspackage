goog.require('simple.exponential');
goog.require('goog.testing.jsunit');

goog.exportSymbol('test_simpleExponential', function () {
    assertEquals(0, simple.exponential(1));
    assertEquals(1, simple.exponential(1));
    assertEquals(2, simple.exponential(4));
    assertEquals(3, simple.exponential(9));
});

