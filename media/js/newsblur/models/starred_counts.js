NEWSBLUR.Models.StarredFeed = Backbone.Model.extend({
    
    initialize: function() {
        this.set('feed_title', this.get('tag'));
        this.views = [];
    },
    
    is_social: function() {
        return false;
    },
    
    is_feed: function() {
        return false;
    },
    
    is_starred: function() {
        return true;
    },
    
    unread_counts: function() {
        return {
            ps: this.get('count') || 0,
            nt: 0,
            ng: 0
        };
    },
    
    tag_slug: function() {
        return Inflector.sluggify(this.get('tag'));
    }
    
});

NEWSBLUR.Collections.StarredFeeds = Backbone.Collection.extend({
    
    model: NEWSBLUR.Models.StarredFeed,
    
    parse: function(models) {
        _.each(models, function(feed) {
            feed.id = 'starred:' + feed.tag;
            // feed.selected = false;
            feed.ps = feed.count;
        });
        
        // Remove below, only used for transition to tag/feed_id.
        models = _.filter(models, function(feed) {
            return feed['tag'];
        });
        return models;
    },
    
    comparator: function(a, b) {
        var sort_order = NEWSBLUR.reader.model.preference('feed_order');
        var title_a = a.get('feed_title') || '';
        var title_b = b.get('feed_title') || '';
        title_a = title_a.toLowerCase();
        title_b = title_b.toLowerCase();

        if (sort_order == 'MOSTUSED') {
            var opens_a = a.get('count');
            var opens_b = b.get('count');
            if (opens_a > opens_b) return -1;
            if (opens_a < opens_b) return 1;
        }
        
        // if (!sort_order || sort_order == 'ALPHABETICAL')
        if (title_a > title_b)      return 1;
        else if (title_a < title_b) return -1;
        return 0;
    },
    
    selected: function() {
        return this.detect(function(feed) { return feed.get('selected'); });
    },
    
    deselect: function() {
        this.chain().select(function(feed) { 
            return feed.get('selected'); 
        }).each(function(feed){ 
            feed.set('selected', false); 
        });
    },
    
    all_tags: function() {
        return this.pluck('tag');
    }
    
});