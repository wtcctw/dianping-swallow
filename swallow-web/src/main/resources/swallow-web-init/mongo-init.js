db = db.getSiblingDB('swallowwebapplication');
result = db.swallowwebalarmmetac.findOne();
if(result == null) {
	load("swallow-mongo-alarmmeta.js");
}
load("swallow-mongo-alarmsetting.js");