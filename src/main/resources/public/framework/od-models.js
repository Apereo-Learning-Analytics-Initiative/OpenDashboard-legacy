var ContextMapping = function(json) {
    this.id = json.id;
    this.key = json.key;
    this.dashboards = json.dashboards;
    this.context = json.context;
    this.modified = json.modified;
};
ContextMapping.prototype = {};