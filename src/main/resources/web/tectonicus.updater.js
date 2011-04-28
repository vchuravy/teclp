/**
 * @author DarkLiKally
 */

function TectonicusDOMUpdater() {}

TectonicusDOMUpdater.prototype = {
    timeInterval: {},
    dayTime: 0,
    worldName: function(world) {
        jQuery("#sidebarWrapper #sidebarContainer .sidebarHeading span:first", "#pageWrapper")
            .text(world);
        return true;
    },
    worldSelection: function(worlds, world) {
        jQuery("#sidebarWrapper #sidebarContainer #worldSelectionWrapper #worldSelection option")
            .remove();
        jQuery.each(worlds, function(index, cWorld) {
            var worldOption = jQuery("<option/>")
                .val(index).text(index);
            if(index == world)
                worldOption.attr("selected", "selected");
            jQuery("#sidebarWrapper #sidebarContainer #worldSelectionWrapper #worldSelection")
                .append(worldOption);
        });
        return true;
    },
    worldTime: function(time) {
        var me = this;
        try {
            clearInterval(this.timeInterval);
        } catch(e){}
        jQuery("#sidebarWrapper #sidebarContainer #worldTimeWrapper div#worldTimeImg #worldTimeImgPointer")
            .css('left', ((time / 24000) * 260));

        jQuery("#sidebarWrapper #sidebarContainer #worldTimeWrapper div#worldTimeDigit")
            .text(time);

        this.dayTime = time;
        this.timeInterval = setInterval(function() {
            if(me.dayTime >= 24000)
                me.dayTime = 0;
            me.dayTime = me.dayTime + 5.18;

            jQuery("#sidebarWrapper #sidebarContainer #worldTimeWrapper div#worldTimeImg #worldTimeImgPointer")
                .css('left', ((me.dayTime / 24000) * 260));

            var currTime = (me.dayTime / 1000) + 6;
            var hour = Math.floor(currTime);
            var minutes = Math.round((currTime - (Math.floor(currTime))) * 59);

            if(minutes < 10) {
                minutes = "0" + minutes;
            }
            if(hour >= 24)
                hour = hour - 24;

            jQuery("#sidebarWrapper #sidebarContainer #worldTimeWrapper div#worldTimeDigit")
                .text(hour + ":" + minutes + " (" + Math.round(me.dayTime) + ")");
        }, 250);
        return true;
    },
    playerList: function(players) {
        jQuery("span, br", "#pageWrapper #sidebarWrapper #sidebarContainer #playerListWrapper")
            .remove();
        jQuery.each(players, function(index, player) {
            var playerLink = jQuery("<a/>")
                .text(index).attr('title', index);
            var playerTrackBox = jQuery("<input/>")
                .val(index).attr('type', 'radio').attr('name', 'trackPlayer');
            var playerElem = jQuery("<span/>")
                .addClass("player player-" + index)
                .append(playerLink).append(playerTrackBox);
            jQuery("#sidebarWrapper #sidebarContainer #playerListWrapper", "#pageWrapper")
                .append("<br />").append(playerElem);
        });
        return true;
    },
    specialPlacesList: function(places) {
        jQuery("span, br", "#pageWrapper #sidebarWrapper #sidebarContainer #specialPlacesListWrapper")
            .remove();
        var length = 0;
        jQuery.each(places, function(index, place) {
            var placeLink = jQuery("<a/>")
                .text(index).attr('title', index);
            var placeElem = jQuery("<span/>")
                .addClass("place place-" + index)
                .append(placeLink);
            jQuery("#sidebarWrapper #sidebarContainer #specialPlacesListWrapper", "#pageWrapper")
                .append("<br />").append(placeElem);
            length++;
        });
        if(length <= 0) {
            jQuery("#pageWrapper #sidebarWrapper #sidebarContainer #specialPlacesListWrapper")
                .append("<br />").append('<span class="specialText">No special places in this world</span>');
        }
        return true;
    },
    wgregionsList: function(regions) {
        jQuery("span, br", "#pageWrapper #sidebarWrapper #sidebarContainer #wgregionsListWrapper")
            .remove();
        var length = 0;
        jQuery.each(regions.pRegions, function(index, region) {
            var regionLink = jQuery("<a/>")
                .text(index).attr('title', index).attr('regiontype', 'poly');
            var regionElem = jQuery("<span/>")
                .addClass("region region-" + index)
                .append(regionLink);
            jQuery("#sidebarWrapper #sidebarContainer #wgregionsListWrapper", "#pageWrapper")
                .append("<br />").append(regionElem);
            length++;
        });
        jQuery.each(regions.cRegions, function(index, region) {
            var regionLink = jQuery("<a/>")
                .text(index).attr('title', index).attr('regiontype', 'cuboid');
            var regionElem = jQuery("<span/>")
                .addClass("region region-" + index)
                .append(regionLink);
            jQuery("#sidebarWrapper #sidebarContainer #wgregionsListWrapper", "#pageWrapper")
                .append("<br />").append(regionElem);
            length++;
        });
        if(length <= 0) {
            jQuery("#pageWrapper #sidebarWrapper #sidebarContainer #wgregionsListWrapper")
                .append("<br />").append('<span class="specialText">No WorldGuard regions in this world</span>');
        }
        return true;
    },
    chatLog: function(chatlog) {
        
    },
    hideSidebarElem: function(elem) {
        jQuery("#" + elem, "#pageWrapper #sidebarWrapper #sidebarContainer").hide();
    } 
}

function TectonicusPlayerMarker(map, world, player, pos, signWindow) {
    var marker = new google.maps.Marker(
            {
                position: pos,
                map: map,
                title: player.name,
                icon: world + '/Images/'+player.name+'.png'
            });
            marker.player = player;

            google.maps.event.addListener(marker, 'click', function() {
                if (player.donation == '') {
                    jQuery.getJSON("http://www.triangularpixels.com/Tectonicus/Query/getDonation.py?json_callback=?",
                    {
                        username: player.name
                    }, function(data) {
                        player.donation = data.html

                        document.getElementById("donationDiv").innerHTML = getDonationHtml(player)
                    });
                }

                showPlayerHtml(map, world, this.player, signWindow, this);
            });
            return marker
}
TectonicusPlayerMarker.prototype = {
    showPlayerHtml: function(map, world, player, signWindow, anchor) {
        var options =
        {
            content: getPlayerHtml(world, player)
        };
        signWindow.close();
        signWindow.setOptions(options);
        signWindow.open(map, anchor);
    },
    getPlayerHtml: function(world, player) {
        var html = ''
        + '<div>'
    
        + '<div class=\"playerName\" style=\"text-align:center; font-size:110%\" >' + player.name + '</div>'
        + '<div style=\"width:300px; margin:4px;\" >'
        +   '<img style=\"float:left; margin:4px;\" src=\"' + world + '/Images/'
                + player.name + '.png\" width=\"32\" height=\"64\" />'
        +   '<div>'
        +       '<div>' + getHealthHtml(player) + '</div>'
        +       '<div>' + getAirHtml(player) + '</div>'
        +       '<div>' + getInventoryHtml(player) + '</div>'
        +       '<div>' + getItemsHtml(player) + '</div>'
        +   '</div>'
        + '</div>'
    
        + '<div id=\"donationDiv\" style=\"clear:both; text-align:center\" >'
        +   getDonationHtml(player)
        + '</div>'
    
        + '</div>'
    
        return html;
    },
    getHealthHtml: function(player) {
        var html = ''
    
        var NUM_ICONS = 10;
        for (var i=0; i<NUM_ICONS; i++) {
            var image;
            if (i*2+1 < player.health)
                image = 'Images/FullHeart.png'
            else if (i*2 < player.health)
                image = 'Images/HalfHeart.png'
            else
                image = 'Images/EmptyHeart.png'
    
            html += '<img style=\"margin:1px\" src=\"' + image + '" width=\"18\" height=\"18\" />'
        }
    
        return html
    },
    getAirHtml: function(player) {
        var html = ''
    
        var NUM_ICONS = 10;
        for (var i=0; i<NUM_ICONS; i++) {
            var image;
            if (i*30 < player.air)
                image = 'Images/FullAir.png'
            else
                image = 'Images/EmptyAir.png'
    
            html += '<img style=\"margin:1px\" src=\"' + image + '" width=\"18\" height=\"18\" />'
        }
    
        return html
    },
    getInventoryHtml: function(player) {
        var html = ''
        /*
         html += '<table>'
    
         html += '<tr>'
    
         for (var x=0; x<9; x++)
         {
         html += '<td>'
         html += 'item'
         html += '</td>'
         }
    
         html += '</tr>'
    
         html += '</table>'
         */
        return html
    },
    getItemsHtml: function(player) {
        var html = ''
    
        // ..
    
        return html
    },
    getDonationHtml: function(player) {
        var html = ''
    
        html += '<div>'
    
        if (player.donation != '') {
            html += player.donation
        } else {
            html += '<img src=\"Images/Spacer.png\" style=\"height:38px;\" />'
        }
    
        html += '</div>'
    
        return html
    }
}
