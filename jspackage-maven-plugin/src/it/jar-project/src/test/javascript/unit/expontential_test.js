goog.require('simple.exponential');
goog.require('goog.testing.jsunit');

goog.exportSymbol('test_simpleExponential', function () {
    assertEquals(0, simple.exponential(0));
    assertEquals(1, simple.exponential(1));
    assertEquals(16, simple.exponential(4));
    assertEquals(81, simple.exponential(9));
});

