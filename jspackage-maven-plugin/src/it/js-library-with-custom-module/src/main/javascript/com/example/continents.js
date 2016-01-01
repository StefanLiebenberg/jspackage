goog.provide('com.example.Continent');
goog.require('com.example.hash');

/**
 * @enum {string}
 */
com.example.Continent = {
  AFRICA: com.example.hash("Africa"),
  EUROPE: com.example.hash("Europe"),
  ASIA: com.example.hash("Asia"),
  AMERICA: com.example.hash("America"),
  ANTARCTICA: com.example.hash("Antarctica"),
  AUSTRALIA: com.example.hash("Australia")
};

/**
 * @return {com.example.Continent}
 */
com.example.africa = function () {
  return com.example.Continent.AFRICA;
};

/**
 * @return {com.example.Continent}
 */
com.example.europe = function () {
  return com.example.Continent.EUROPE;
};

/**
 * @return {com.example.Continent}
 */
com.example.asia = function () {
  return com.example.Continent.ASIA;
};

/**
 * @return {com.example.Continent}
 */
com.example.america = function () {
  return com.example.Continent.AMERICA;
};

/**
 * @return {com.example.Continent}
 */
com.example.antarctica = function () {
  return com.example.Continent.ANTARCTICA;
};

/**
 * @return {com.example.Continent}
 */
com.example.australia = function () {
  return com.example.Continent.AUSTRALIA;
};