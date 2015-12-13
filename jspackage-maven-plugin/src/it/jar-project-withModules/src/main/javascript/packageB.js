goog.provide('com.domain.packageB');
goog.require('goog.Disposable');

/**
 * @constructor
 * @extends {goog.Disposable}
 */
com.domain.packageB = function () {
  com.domain.packageB.base(this, 'constructor');
};
goog.inherits(com.domain.packageB, goog.Disposable);