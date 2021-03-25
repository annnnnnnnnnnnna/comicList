function Platform(id) {
    var self = this;
    self.titles = ko.observableArray();
    self.id = id;
    self.name = ko.observable();
    self.chked = ko.observable();
    self.lastUpdatedAt = ko.observable();

    self.loadTitles = function(){
        if (self.titles().length == 0) {
            $.getJSON("/titleSelect/getTitles",
                { platformId: self.id },
                function(result) {
                    var titles = $.map(result, function(title) { return new Title(title);});
                    self.titles(titles);
                    displayChange(self.id);
                }
            );
        } else {
            displayChange(self.id);
        }
    };
}

function Title(data) {
    var self = this;
    self.id = ko.observable(data.id);
    self.name = ko.observable(data.name);
    self.lastUpdatedAt = ko.observable(data.updateDate);
    self.chked = (read.indexOf(data.id.toString()) != -1);
}

var platforms = [];
function loaded() {
    Array.from(document.getElementsByClassName("titles")).forEach(function(element) {
        var platform = new Platform(element.id);
        platforms[element.id] = platform;
        ko.applyBindings(platform, element);
    });
}

function loadTitles(id) {
    platforms[id].loadTitles();
}

function displayChange(id) {
    var oelm = document.getElementById('t_'+id);
    var txt = oelm.innerHTML;
    if(txt.indexOf("▶") != -1) oelm.innerHTML = txt.replace("▶", "▼");
    else oelm.innerHTML = txt.replace("▼", "▶");

    var elm = document.getElementById(id);
    var v = elm.style.display;
    var next = "none";
    if (v == "none") next = "block";
    elm.style.display = next;
}
function change(e) {
    if (e.checked) read.push(e.value);
    else read.pop(e.value);

	var titles = read.join('-');
    document.cookie = 'titles=' + titles;
}

var tmp = document
              .cookie
              .split('; ')
              .find(row => row.startsWith('titles'));
var read = [];
if (tmp != "") {
    read = tmp.split('=')[1].split('-').filter(v => v != '');
}
