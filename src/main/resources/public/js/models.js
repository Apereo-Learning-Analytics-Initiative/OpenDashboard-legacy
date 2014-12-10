var ContextMapping = function(json) {
	this.id = json.id;
	this.key = json.key;
	this.context = json.context;
	this.modified = json.modified;
};
ContextMapping.prototype = {};

var Card = function(json) {
	this.id = json.id;
	this.name = json.name;
	this.description = json.description;
	this.imgUrl = json.imgUrl;
	this.cardType = json.cardType;
	this.config = json.config;
};
Card.prototype = {};

var CardInstance = function(json) {
	this.id = json.id;
	this.cardId = json.cardId;
	this.context = json.context;
	this.name = json.name;
	this.description = json.description;
	this.imgUrl = json.imgUrl;
	this.cardType = json.cardType;
	this.config = json.config;
	this.sequence = json.sequence;
};
CardInstance.prototype = {
	setCard: function(card) {
		this.cardId = card.id;
		this.name = card.name;
		this.description = card.description;
		this.imgUrl = card.imgUrl;
		this.cardType = card.cardType;
		this.config = card.config;
	},
	setConfig: function(config) {
		this.config = config;
	}
};
