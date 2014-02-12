Venues = new Meteor.Collection('venues');
Tabs = new Meteor.Collection('tabs');

if (Meteor.isClient) {
    Meteor.subscribe('venues');
    Meteor.subscribe('tabs');

    function extractData(form) {
        var data = _.reduce($(form).serializeArray(), function (memo, item) {
            memo[item.name] = item.value;
            return memo;
        }, {});
        return data;
    }

    Template.venues.venues = function () {
        return Venues.find();
    }

    Template.newVenue.events({
        'submit': function (e, tmpl) {
            var venue = extractData(e.currentTarget);
            Venues.insert(venue);
            return false;
        }
    });

    Template.venue.events({
        'click li': function (e, tmpl) {
            Session.set('selectedVenue', tmpl.data);
        }
    });

    Template.editVenue.venue = function () {
        return Session.get('selectedVenue');
    };

    Template.editVenue.events({
        'submit': function (e, tmpl) {
            var venue = extractData(e.currentTarget);
            Venues.update({_id: venue._id}, {
                $set: {name: venue.name}
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
            // if(_.isArray(data)){
            //   _.each(data, function(tab){
            //     Tabs.insert(tab);
            //   });
            //   return true;
            // }else if(_.isObject(data)){
            //   Tabs.insert(data);
            //   return true;
            // }
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
            return false;
        }
    })

    Venues.allow({
        insert: function () {
            return true;
        },
        update: function () {
            return true;
        },
        remove: function () {
            return false;
        }
    })
}
