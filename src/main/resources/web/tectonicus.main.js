/**
 * @author DarkLiKally
 */

function TectonicusMap(newSettings) {
    jQuery.extend(true, this.settings, this.baseSettings, newSettings);
    this.status.createdMapObject = true;
    this.domUpdater = new TectonicusDOMUpdater();

    if(this.settings.autoInit) {
        this.init();
    }
}

TectonicusMap.prototype = {
    settings: {},
    status: {
        createdMapObject: false,
        firstLoadedWorldData: false
    },
    minecraftMapType: [],
    spawnMarker: null,
    signMarkers: [],
    playerMarkers: [],
    specialPlaceMarkers: [],
    wgregionMarkers: [],
    baseSettings: {
        mapTypeSettings: {
            getTileUrl: function(coord, zoom) {
                return "data/"+ Map.worldData.name
                    + "/Zoom" + zoom
                    + "/tile_"
                    + coord.x + "_" + coord.y + "."
                    + Map.worldData.tectonicus.tiletype;
            },
            tileSize: new google.maps.Size(512, 512),
            isPng: false
        },
        mapSettings: {
            zoom: 1,
            center: new google.maps.LatLng(0,0),
            mapTypeId: google.maps.MapTypeId.ROADMAP,
            mapTypeControl: false,
            streetViewControl: false,
            mapTypeControlOptions: {
                mapTypeIds: ['minecraft', google.maps.MapTypeId.ROADMAP],
                style: google.maps.MapTypeControlStyle.DROPDOWN_MENU
            }
        },
        autoInit: false,
        serverUrl: "."
    },
    init: function() {
        if(!this.status.createdMapObject)
            return false;

        this.loadWorldData("", true);

        return true;
    },
    loadWorldData: function(worldName, setupNewMap, asyncr) {
        var me = this;
        if(setupNewMap == null)
            setupNewMap = false;

        if(worldName == null)
            worldName = "";

        jQuery.ajax({
            url: me.settings.serverUrl + "/json/getData.json",
            context: me,
            async: asyncr,
            dataType: 'json',
            cache: false,
            data: {world: worldName, ajax: true, version: '1.0b', 't': new Date().getTime()},
            timeout: 5000,
            success: function(data) {
                me.worldData = data;

                if(me.worldData.tectonicus.tiletype == 'png')
                    me.settings.mapTypeSettings.isPng = true;

                me.loadWorldScript(me.worldData.name, 'signs', asyncr, function(loadedSigns) {
                    if(loadedSigns) {
                        me.loadWorldScript(me.worldData.name, 'stats', asyncr, function(loadedStats) {
                            if(loadedStats) {
                                me.status.firstLoadedWorldData = true;
                                me.updateDOM(loadedStats);

                                if(setupNewMap)
                                    me.setupMap();
                            }
                        });
                    }
                });
            },
            error: function(a, tStatus, eThrown) {
                alert("Could not load the world data from the server " + me.settings.serverUrl);
                return false;
            }
        });
    },
    reloadWorldData: function(world) {
        this.loadWorldData(world, false, false);
    },
    changeWorld: function(world) {
        var me = this;
        if(me.status.createdMapObject && me.status.firstLoadedWorldData) {
            me.loadWorldData(world, false, false);
            me.map.setMapTypeId('minecraft' + me.worldData.name);

            return true;
        }

        return false;
    },
    loadWorldScript: function(worldName, scriptName, asyncr, callback) {
        var me = this;
        
        jQuery.ajax({
            url: 'data/' worldName + '/' + scriptName + '.js',
            dataType: 'script',
            cache: false,
            async: asyncr,
            success: function() {
                callback(true);
                return true;
            },
            error: function() {
                alert("Could not load the script data " + scriptName + " for world " + worldName);
                callback(false);
                return false;
            }
        });
    },
    setupMap: function() {
        var me = this;
        if(!me.status.firstLoadedWorldData)
            return false;

        jQuery.each(me.worldData.worlds, function(index, worldNum) {
            me.minecraftMapType[index] = new google.maps.ImageMapType(
                jQuery.extend(me.settings.mapTypeSettings, {maxZoom: me.worldData.tectonicus.maxzoom}));
            me.minecraftMapType[index].projection = new MinecraftMapProjection();
        });

        var centerLatLng = me.minecraftMapType[me.worldData.name].projection.fromPointToLatLng(worldToMap(
            me.worldData.spawn.x,
            me.worldData.spawn.y,
            me.worldData.spawn.z
        ));

        var startSettings = {}

        jQuery.extend(true, startSettings, me.settings.mapSettings, {mapSettings: {center: centerLatLng}});

        var map = me.map = new google.maps.Map(document.getElementById("map_canvas"), startSettings);

        //var worlds = [];
        jQuery.each(me.worldData.worlds, function(index, worldNum) {
            //worlds.push('minecraft' + index, me.minecraftMapType);
            map.mapTypes.set('minecraft' + index, me.minecraftMapType[index]);
        });

        //map.mapTypes.set('minecraft' + me.worldData.name, me.minecraftMapType);
        //map.mapTypes.set(worlds);
        map.setMapTypeId('minecraft' + me.worldData.name);

        me.signWindow = new google.maps.InfoWindow({});

        google.maps.event.addListener(map, 'projection_changed', function() {
            me.registerSidebarEvents();
            me.updateMarkers();
        });

        me.runAnalytics();
        return true;
    },
    updateDOM: function() {
        var me = this;

        me.domUpdater.worldName(me.worldData.name);
        me.domUpdater.worldSelection(me.worldData.worlds, me.worldData.name);
        me.domUpdater.worldTime(me.worldData.time);
        me.domUpdater.playerList(me.worldData.players);
        if(me.worldData.tectonicus.placesinitiallyvisible)
            me.domUpdater.specialPlacesList(me.worldData.specialplaces);
        else
            me.domUpdater.hideSidebarElem("specialPlacesListWrapper");
        if(me.worldData.tectonicus.regionsinitiallyvisible)
            me.domUpdater.wgregionsList(me.worldData.wgregions);
        else
            me.domUpdater.hideSidebarElem("wgregionsListWrapper");
    },
    registerSidebarEvents: function() {
        var me = this;

        jQuery("a", "#sidebarWrapper #sidebarContainer #wgregionsListWrapper", "#pageWrapper")
            .live('click', function(event) {
                me.clearWgRegionOnMap();
                me.addWgRegionToMap(jQuery(this).attr('title'), jQuery(this).attr('regiontype'), me.map.getProjection());
            });
    },
    clearWgRegionOnMap: function() {
        var me = this;

        jQuery.each(me.wgregionMarkers, function(index, marker) {
            marker.setMap(null);
        });
        me.wgregionMarkers = [];
    },
    addWgRegionToMap: function(regionId, regionType, projection) {
        var me = this;

        if(regionType == "poly")
            var region = me.worldData.wgregions.pRegions[regionId];
        else if(regionType == "cuboid")
            var region = me.worldData.wgregions.cRegions[regionId];
        else return false;

        if(regionType == "cuboid") {
            region.points = [
                {"x": region.min.x, "z": region.min.z},
                {"x": region.max.x, "z": region.min.z},
                {"x": region.max.x, "z": region.max.z},
                {"x": region.min.x, "z": region.max.z}
            ];
            region.minY = Math.min(region.min.y, region.max.y);
            region.maxY = Math.max(region.min.y, region.max.y);
        }

        jQuery.each(region.points, function(index, regionPoint) {
            var point = worldToMap(regionPoint.x, region.maxY - ((region.maxY - region.minY) / 2), regionPoint.z);
            var pos = projection.fromPointToLatLng(point);
            var newMarker = me.generateMarker({
                position: pos,
                map: me.map,
                title: 'Region ' + regionId + ' point #' + index,
                icon: 'Images/IronIcon.png'
            });

            newMarker.wgregion = region;
            newMarker.wgregionPoint = index;

            google.maps.event.addListener(newMarker, 'click', function() {
                var options = {
                    content: '<pre><center>' + 'Region ' + this.wgregion.id + ' point #' + this.wgregionPoint + '</pre></center>'
                };
                me.signWindow.close();
                me.signWindow.setOptions(options);
                me.signWindow.open(me.map, this);
            });
            me.wgregionMarkers.push(newMarker);
        });
    },
    updateMarkers: function() {
        var me = this;

        jQuery.each(me.signMarkers, function(index,value) {
            value.setMap(null);
        });
        jQuery.each(me.playerMarkers, function(index,value) {
            value.setMap(null);
        });
        jQuery.each(me.specialPlaceMarkers, function(index,value) {
            value.setMap(null);
        });
        jQuery.each(me.wgregionMarkers, function(index,value) {
            value.setMap(null);
        });

        me.updateSpawnMarker(me.map.getProjection());
        me.updateSignMarkers(me.map.getProjection());
        me.updatePlayerMarkers(me.map.getProjection());
        me.updateSpecialPlacesMarkers(me.map.getProjection());
    },
    updateSpawnMarker: function(projection) {
        var me = this;

        if(me.worldData.tectonicus.showspawn) {
            try {
                me.spawnMarker.setMap(null);
            } catch(e) {
                me.spawnMarker = null;
            }
            var point = worldToMap(me.worldData.spawn.x, me.worldData.spawn.y, me.worldData.spawn.z);
            var pos = projection.fromPointToLatLng(point);

            var marker = new google.maps.Marker({
                position: pos,
                zIndex: 1000,
                map: me.map,
                title: 'Spawn point',
                icon: 'Images/Spawn.png'
            });

            google.maps.event.addListener(marker, 'click', function() {
                var statsHtml = '';

                if(me.worldData.name != '') {
                    statsHtml += '<div><center><font size="+2">' + me.worldData.name + '</font></center></div>';
                }

                statsHtml += '<div><center>World Stats</center></div>';
                statsHtml += 'Total players: ' + me.worldData.players.length + '<br />';
                statsHtml += '<br />';

                statsHtml += '<div><center>Blocks</center></div>'
                for (i in statsData) {
                    var stat = statsData[i];
                    statsHtml += stat.name + ' ' + stat.count + '<br />'
                }

                var options = {
                    content: statsHtml
                };

                me.signWindow.close();
                me.signWindow.setOptions(options);
                me.signWindow.open(me.map, this);
            });
            me.spawnMarker = marker;
        }
    },
    updateSignMarkers: function(projection) {
        var me = this;

        for(i in signData) {
            var sign = signData[i];
            var signPoint = worldToMap(sign.worldX, sign.worldY, sign.worldZ);
            var signPos = projection.fromPointToLatLng(signPoint);

            var signMarker = new google.maps.Marker({
                position: signPos,
                map: me.map,
                title: '',
                icon: 'Images/Sign.png'
            });

            // Disable this marker if we don't want signs initially visible
            if (!me.worldData.tectonicus.signsinitiallyvisible)
                signMarker.setMap(null)

            signMarker.sign = sign; // save this ref in the marker so we can fetch it in the bound function below

            google.maps.event.addListener(signMarker, 'click', function() {
                var options = {
                    content: '<pre><center>' + this.sign.text1 + '<br />' + this.sign.text2 + '<br />' + this.sign.text3 + '<br />' + this.sign.text4 + '</pre></center>'
                };
                me.signWindow.close();
                me.signWindow.setOptions(options);
                me.signWindow.open(me.map, this);
            });
            me.signMarkers.push(signMarker);
        }
    },
    updatePlayerMarkers: function(projection) {
        var me = this;

        
    },
    updateSpecialPlacesMarkers: function(projection) {
        var me = this;

        jQuery.each(me.worldData.specialplaces, function(index, place) {
            var placePoint = worldToMap(place.x, place.y, place.z);
            var placePos = projection.fromPointToLatLng(placePoint);

            var placeMarker = new google.maps.Marker({
                position: placePos,
                map: me.map,
                title: index,
                icon: 'Images/DiamondIcon.png'
            });

            if(!me.worldData.tectonicus.placesinitiallyvisible)
                placeMarker.setMap(null);

            placeMarker.place = place;
            placeMarker.place.name = index;

            google.maps.event.addListener(placeMarker, 'click', function() {
                var options = {
                    content: '<strong>' + this.place.name + '</strong><br />X: ' + this.place.x + '<br />Y: ' + this.place.y + '<br />Z: ' + this.place.z
                };
                me.signWindow.close();
                me.signWindow.setOptions(options);
                me.signWindow.open(me.map, this);
            });

            me.specialPlaceMarkers.push(placeMarker);
        });
    },
    generateMarker: function(options) {
        var marker = new google.maps.Marker(options);

        return marker;
    },
    trackPlayer: function(playerName) {
        var me = this;
        
    },
    runAnalytics: function() {
        var _gaq = _gaq || [];
        _gaq.push(['_setAccount', 'UA-4472611-2']);
        _gaq.push(['_setDomainName', 'tectonicus.triangularpixels.com']);
        _gaq.push(['_trackPageview']);

        var selfUrl = window.location.href;
        _gaq.push(['_trackEvent', 'ViewMap', selfUrl]);

        (function() {
            var ga = document.createElement('script');
            ga.type = 'text/javascript';
            ga.async = true;
            ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
            var s = document.getElementsByTagName('script')[0];
            s.parentNode.insertBefore(ga, s);
        })();
    }
}