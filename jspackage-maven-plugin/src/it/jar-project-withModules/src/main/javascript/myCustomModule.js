goog.module("customModule");
goog.require('com.domain.packageA');

/**
 *
 */
goog.exportSymbol("myCustomModule", function () {
  var packageA = new dom.domain.packageA();
  packageA.foo();
});