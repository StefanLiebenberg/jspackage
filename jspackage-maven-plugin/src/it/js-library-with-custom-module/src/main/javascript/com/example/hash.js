goog.provide('com.example.hash');
goog.require('goog.crypt.Sha1');

/**
 * @param {string} value
 */
com.example.hash = function (value) {
  var sha1 = new goog.crypt.Sha1();
  sha1.update(value);
  return sha1.digest();
};