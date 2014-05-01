Tabs = new Meteor.Collection('tabs');

if (Meteor.isClient) {
    Meteor.subscribe('tabs');

    function extractData(form) {
        return _.reduce($(form).serializeArray(), function (memo, item) {
            memo[item.name] = item.value;
            return memo;
        }, {});
    }

    Template.tabs.tabs = function () {
        return Tabs.find();
    }

    Template.newTab.events({
        'submit': function (e, tmpl) {
            var tab = extractData(e.currentTarget);
            tab.total = Number(tab.total);
            Tabs.insert(tab);
            return false;
        }
    });

    Template.tab.events({
        'click li': function (e, tmpl) {
            Session.set('selectedTab', tmpl.data);
        }
    });

    Template.editTab.tab = function () {
        return Session.get('selectedTab');
    };

    Template.editTab.events({
        'submit': function (e, tmpl) {
            var tab = extractData(e.currentTarget);
            tab.total = Number(tab.total);
            Tabs.update({_id: tab._id}, {
                $set: {name: tab.name, total: tab.total}
            });
            return false;
        }
    });
}

if (Meteor.isServer) {
    console.log('started server');

    Meteor.publish('tabs', function (id) {
        console.log('subscribing to tabs', id);
        return Tabs.find();
    });

    Meteor.methods({
        'addTab': function (tab1, tab2, tab3) {
            console.log('attempting to add a tab');
            console.log(arguments);
            if (_.isObject(tab1)) {
                Tabs.insert(tab1);
            }
            if (_.isObject(tab2)) {
                Tabs.insert(tab2);
            }
            if (_.isObject(tab3)) {
                Tabs.insert(tab3);
            }
            return true;
        }
    });

    Meteor.startup(function () {
        // code to run on server at startup
    });

    Meteor.onConnection(function (conn) {
        console.log(conn);
        conn.onClose(function () {
            console.log('connection to ', conn.id, ' closed');
        });
    });

    Tabs.allow({
        insert: function () {
            return true;
        },
        update: function () {
            return true;
        },
        remove: function () {
            return true;
        }
    })

}
