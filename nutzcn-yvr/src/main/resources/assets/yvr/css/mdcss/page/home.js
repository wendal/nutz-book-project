$(document).ready(function () {

    // 查找当前环境下的配置文件
    var hconf = $.extend(true, {
        profile: {
            name: "未登录",
            email: "无法获取邮箱信息",
            avatar: "" // 这里给个img的url即可
        },
        nav_user: [],
        nav_main: [],
        actions: {},
        closeQuick: false
    }, _md_page_home_ || {});

    var mphome = {
        components: {
            'header': $('#md-page-header'),
            'modulelabel': $('#module-label'),
            'modulelabelFront': $('#module-label > .front'),
            'modulelabelBack': $('#module-label > .back'),
            'info': $('.mp-info'),
            'nav': $('#md-page-nav'),
            'navBtnGroup': $('#md-page-header .nav-btn-group'),
            'navSwitch': $('.nav-switch'),
            'profile': $('#md-page-nav .user-profile'),
            'userMenu': $('#user-menu'),
            'mainMenu': $('#main-menu'),
            'userMenuSwitch': $('.user-menu-switch'),
            'main': $('#md-page-main'),
            'mainOverlay': $('#md-page-main-overlay'),
            'extSwtich': $('#md-page-header .ext-switch'),
            'extBtnGroup': $('#md-page-header .ext-btn-group'),
            'extTabGroup': $('#ext-tab-group'),
            'extTabGroupBBar': $('#ext-tab-group .tab-bottom-bar'),
            'extTabContainerGroup': $('#ext-tab-container-group'),
            'extTabViewpage': $('#ext-tab-container-group .ext-tab-container-group-viewpage'),
            'ext': $('#md-page-extension'),
            'extOverlay': $('#md-page-extension-overlay'),
            'extOverlayLoading': $('#md-page-extension-overlay-loading'),
            'sysLoading': $('#md-system-loading'),
            'sysLoadingTip': $('#md-system-loading-tip'),
            'sysLoadingTipLabel': $('#md-system-loading-tip .tip-label'),
            'mainFab': $('#md-floating-action-button')
        },
        cons: {
            'liHeight': 48
        },
        setUlHeight: function ($ul) {
            var lisize = $ul.children('li').length;
            $ul[0].style.height = lisize * mphome.cons.liHeight + 'px';
        },
        clearUlHeight: function ($ul) {
            $ul[0].style.height = 0;
        },
        urlArgs: function (query) {
            var args = {};
            var pairs = query.split('&');
            for (var i = 0; i < pairs.length; i++) {
                var pos = pairs[i].indexOf('=');
                if (pos == -1) {
                    continue;
                }
                var name = pairs[i].substr(0, pos);
                var value = pairs[i].substr(pos + 1);
                value = decodeURIComponent(value);
                args[name] = value;
            }
            return args;
        },
        cache: {
            url: {}
        },
        action: {}
    };

    // 加载profile信息
    mphome.profile = {
        'load': function (callback) {
            if (mphome.components.profile.length > 0) {
                if ($.isFunction(hconf.profile)) {
                    hconf.profile(function (re) {
                        mphome.profile.set(re);
                        if (callback) {
                            callback()
                        }
                    });
                } else {
                    mphome.profile.set(hconf.profile);
                    if (callback) {
                        callback()
                    }
                }
            } else {
                if (callback) {
                    callback()
                }
            }
        },
        'set': function (profile) {
            mphome.components.profile.find('.user-name').html(profile.name);
            mphome.components.profile.find('.user-email').html(profile.email);
            if (profile.avatar) {
                mphome.components.profile.find('.user-avatar').attr('src', profile.avatar);
            }
        }
    }

    mphome.mainOverlay = {
        'open': function () {
            mphome.components.mainOverlay.addClass('show');
            mphome.components.mainOverlay.addClass('open');
        },
        'close': function () {
            mphome.components.mainOverlay.removeClass('open');
            setTimeout(function () {
                if (!mphome.components.mainOverlay.hasClass('open')) {
                    mphome.components.mainOverlay.removeClass('show');
                }
            }, 500);
        }
    };

    mphome.ajaxloading = {
        'open': function () {
            mphome.components.header.addClass('ajaxloading');
        },
        'close': function () {
            mphome.components.header.removeClass('ajaxloading');
        },
        'enable': function () {
            $(document).ajaxStart(function () {
                mphome.ajaxloading.open();
            }).ajaxStop(function () {
                setTimeout(function () {
                    mphome.ajaxloading.close();
                }, 1000);
            })
        }
    };

    mphome.module = {
        'showLabel': function (ltxt) {
            var lbnm = mphome.components.modulelabel.attr('labelBlock');
            var isFront = lbnm == 'front';
            var $lb = isFront ? mphome.components.modulelabelFront : mphome.components.modulelabelBack;
            $lb.html(ltxt);
            mphome.components.modulelabel.attr('labelblock', (isFront ? 'back' : 'front'));

            // 切换动画
            var $back = isFront ? mphome.components.modulelabelFront : mphome.components.modulelabelBack;
            var $front = isFront ? mphome.components.modulelabelBack : mphome.components.modulelabelFront;
            $front.removeClass('flipInX').addClass('flipOutX');
            setTimeout(function () {
                mphome.components.modulelabel.toggleClass('flip');
                $back.removeClass('flipOutX').addClass('flipInX');
            }, 500);
        }
    }

    mphome.user = {
        'loadInfo': function (callback) {
            mphome.profile.load(callback);
        }
    };

    mphome.ext = {
        'open': function () {
            mphome.mainOverlay.open();
            mphome.components.extSwtich.addClass('open');
            mphome.components.ext.show();
            mphome.nav.close();
            mphome.components.navBtnGroup.addClass('close');
            setTimeout(function () {
                mphome.components.ext.addClass('open');
            }, 1)
        },
        'close': function () {
            if (mphome.components.extSwtich.hasClass('open')) {
                mphome.components.extSwtich.removeClass('open');
                mphome.components.ext.removeClass('open');
                setTimeout(function () {
                    mphome.components.ext.hide();
                }, 400)
                mphome.mainOverlay.close();
                mphome.components.navBtnGroup.removeClass('close');
            }
        },
        'makeExtBtnHtml': function (tabs) {
            var ebhtml = '';
            for (var i = 0, ml = tabs.length; i < ml; i++) {
                var tab = tabs[i];
                ebhtml += '<li class="md-icon-wrap ext-btn-icon" ext="' + tab.ext + '">';
                if (tab.icon) {
                    if (tab.iconType == 'fa') {
                        ebhtml += '<i class="fa-icon ' + tab.icon + '"></i>';
                    } else if (tab.iconType == 'walnut') {
                        ebhtml += '<i class="walnut-icon ' + tab.icon + '"></i>';
                    } else {
                        ebhtml += '<i class="md-icon ' + tab.icon + '"></i>';
                    }
                }
                ebhtml += '</li>';
            }
            return ebhtml;
        },
        'makeTabHtml': function (tabs) {
            var ebhtml = '';
            for (var i = 0, ml = tabs.length; i < ml; i++) {
                var tab = tabs[i];
                ebhtml += '<li class="ext-tab-label" ext="' + tab.ext + '">';
                ebhtml += '<div class="ripple-button">';
                ebhtml += tab.label;
                ebhtml += '</div>';
                ebhtml += '</li>';
            }
            return ebhtml;
        },
        'makeTabContainerHtml': function (tabs) {
            var ebhtml = '';
            for (var i = 0, ml = tabs.length; i < ml; i++) {
                var tab = tabs[i];
                // TODO 加载对应页面
                ebhtml += '<div class="ext-tab-container" ext="' + tab.ext + '" >'
                ebhtml += '<h1>' + tab.label + '</h1>'
                ebhtml += '</div>'
            }
            return ebhtml;
        },
        'loadTab': function (callback) {

            var tabs = [];

            // 加载tab-btn
            mphome.components.extBtnGroup.append(mphome.ext.makeExtBtnHtml(tabs));
            // 加载tab
            mphome.components.extTabGroup.append(mphome.ext.makeTabHtml(tabs));
            // 加载tab-container
            mphome.components.extTabViewpage.append(mphome.ext.makeTabContainerHtml(tabs));
            //mphome.components.extTabViewpage.css(
            //    'width', tabs.length + "00%"
            //);
            // 计算Tab每个块的宽度
            // 这里因为有padding-left 48px;
            var tleft = 48;
            mphome.components.extTabGroup.find('li').each(function (i, ele) {
                var $li = $(ele);
                var tw = ele.offsetWidth;
                $li.attr('tleft', tleft);
                $li.attr('twidth', tw);
                tleft += tw;
            });

            if (callback) {
                callback();
            }
        }
    }

    mphome.nav = {
        'open': function () {
            mphome.mainOverlay.open();
            mphome.components.navSwitch.addClass('open');
            mphome.components.nav.addClass('open');

        },
        'close': function () {
            if (mphome.components.navSwitch.hasClass('open')) {
                mphome.components.navSwitch.removeClass('open');
                mphome.components.nav.removeClass('open');
                mphome.mainOverlay.close();
            }
        },
        'makeNavHtml': function (mc) {
            var navhtml = '';
            for (var i = 0; i < mc.length; i++) {
                var ni = mc[i];
                navhtml += '<li class="' + (ni.type == 'menu' ? 'has-sub-menu' : '') + '">';
                navhtml += '    <a  class="ripple-button" onclick="return false;" type="' + ni.type + '" label="' + ni.label + '"';
                // 类型
                if (ni.type == 'url') {
                    navhtml += ' url="' + ni.url + '" ';
                    if (ni.args) {
                        var argsStr = '';
                        for (var k in ni.args) {
                            argsStr += "&" + k + "=" + ni.args[k];
                        }
                        navhtml += ' args="' + argsStr.substr(1) + '"';
                    }
                } else if (ni.type == 'action') {
                    navhtml += ' action="' + ni.action + '"';
                }
                navhtml += ' >';
                // 图标
                if (ni.icon) {
                    if (ni.iconType == 'fa') {
                        navhtml += '<i class="stato2-nav-icon fa-icon ' + ni.icon + '"></i>';
                    } else if (ni.iconType == 'walnut') {
                        navhtml += '<i class="stato2-nav-icon walnut-icon ' + ni.icon + '"></i>';
                    } else {
                        navhtml += '<i class="stato2-nav-icon md-icon ' + ni.icon + '"></i>';
                    }
                } else if (ni.img) {
                    navhtml += '<img src="' + ni.img + '" >';
                }
                navhtml += ni.label;
                navhtml += '    </a>';
                // 如果有子菜单
                if (ni.type == 'menu') {
                    navhtml += '<ul class="sub-menu">';
                    navhtml += mphome.nav.makeNavHtml(ni.menu);
                    navhtml += '</ul>';
                }
                navhtml += '</li>';
            }
            return navhtml;
        },
        'loadMenu': function (callback) {
            mphome.nav.loadUserMenu(callback);
        },
        'loadUserMenu': function (callback) {
            if ($.isFunction(hconf.nav_user)) {
                return hconf.nav_user(function (re) {
                    mphome.components.userMenu.append(mphome.nav.makeNavHtml(re));
                    mphome.nav.loadMainMenu(callback);
                });
            } else {
                mphome.components.userMenu.append(mphome.nav.makeNavHtml(hconf.nav_user));
                mphome.nav.loadMainMenu(callback);
            }
        },
        'loadMainMenu': function (callback) {
            if ($.isFunction(hconf.nav_main)) {
                return hconf.nav_main(function (re) {
                    mphome.components.mainMenu.append(mphome.nav.makeNavHtml(re));
                    if (callback) {
                        callback();
                    }
                });
            } else {
                mphome.components.mainMenu.append(mphome.nav.makeNavHtml(hconf.nav_main));
                if (callback) {
                    callback();
                }
            }
        },
        'navAction': function ($a) {
            var acNm = $a.attr('action');
            var acFun = mphome.action[acNm];
            if (acFun) {
                acFun();
            } else {
                alert("未注册事件: " + acNm);
            }
        },
        'navUrl': function (navItem) {
            // 加载页面
            var url = navItem.url;
            var lochref = window.location.href;
            var lhi = lochref.indexOf("#");
            var page = "/walnut/app.jsp";
            var args = null;

            // 清空当前页面
            mphome.components.main[0].innerHTML = '';
            // 显示加载动画

            var isEssApp = !(url[0] == '/');   // 不是以 '/' 开头, 则为app名称
            // ess的app, 格式为app/id:xxxxx
            if (isEssApp) {
                // TODO
                var si = url.indexOf('/');
                var appname = null;
                var obj = null;
                if (si != -1) {
                    appname = url.substr(0, si);
                    // 获取obj对象
                    var objId = url.substr(si + 1);
                    obj = $http.syncGet("/o/get/" + objId).data;
                } else {
                    appname = url;
                }
                args = {};
                args.appname = appname;
                args.obj = obj;
            }
            // 普通页面,  格式为/xxx/xxxx?a=b&c=d
            else {
                page = url;
                if (navItem.args) {
                    url += "?" + navItem.args;
                    args = mphome.urlArgs(navItem.args);
                }

            }
            // 显示module-label
            mphome.module.showLabel(navItem.label);
            // 设置新的href
            window.location.href = (lhi > 0 ? lochref.substr(0, lhi) : lochref) + "#" + url;
            page = (home_base ? home_base : "") + "/page" + page;

            // 读取页面
            $http.getText(page, function (pg) {
                pg += '<script>';
                pg += '$(document).ready(function(){'
                pg += '    myInit(' + (args == null ? '' : JSON.stringify(args)) + ');';
                pg += '});'
                pg += '<' + '/script>';
                // 添加页面到mview中
                // FIXME innerHtml 不会触发事件!
                // mphome.components.main[0].innerHTML = pg;
                mphome.components.main.html(pg);
            });

            // 关闭nav
            setTimeout(function () {
                mphome.nav.close();
            }, 400);
        }
    };

    mphome.userMenu = {
        'open': function () {
            mphome.components.userMenuSwitch.addClass('open');
            mphome.components.userMenu.addClass('open');
            mphome.setUlHeight(mphome.components.userMenu);
        },
        'close': function () {
            mphome.components.userMenuSwitch.removeClass('open');
            mphome.components.userMenu.removeClass('open');
            mphome.clearUlHeight(mphome.components.userMenu);
        }
    };

    mphome.sysTip = {
        'tip': function (tip) {
            mphome.components.sysLoadingTipLabel[0].innerHTML = tip;
        },
        'open': function (tip) {
            mphome.components.sysLoadingTip.find('.tip-label').html(tip);
            mphome.components.sysLoadingTip.removeClass('close');
        },
        'close': function () {
            mphome.components.sysLoadingTip.addClass('close');
        }
    }

    mphome.sysLoading = {
        'open': function (tip) {
            mphome.components.sysLoading.removeClass('close');
            mphome.sysTip.open(tip);
        },
        'close': function () {
            setTimeout(function () {
                mphome.sysTip.close();
                setTimeout(function () {
                    mphome.components.sysLoading.addClass('close');
                }, 500);
            }, 1200);
        },
        'closeQuick': function () {
            mphome.sysTip.close();
            mphome.components.sysLoading.addClass('close');
        }
    }

    mphome.startListen = function () {

        mphome.components.navSwitch.on('click', function () {
            if ($(this).hasClass('open')) {
                mphome.nav.close();
            } else {
                mphome.nav.open();
            }
        });

        mphome.components.extSwtich.on('click', function () {
            if ($(this).hasClass('open')) {
                mphome.ext.close();
            } else {
                mphome.ext.open();
            }
        });

        mphome.components.userMenuSwitch.on('click', function () {
            if ($(this).hasClass('open')) {
                mphome.userMenu.close();
            } else {
                mphome.userMenu.open();
            }
        });

        mphome.components.mainOverlay.on('click', function () {
            mphome.nav.close();
        });

        // 显示/关闭子菜单
        mphome.components.nav.delegate('li.has-sub-menu > a', 'click', function (e) {
            var $li = $(this).parent();
            if ($li.hasClass('open')) {
                $li.removeClass('open');
                mphome.clearUlHeight($li.children('ul'));
            } else {
                $li.addClass('open');
                mphome.setUlHeight($li.children('ul'));
                // 其他打开的关闭掉
                var $siblings = $li.siblings('li.has-sub-menu');
                if ($siblings.length > 0) {
                    $siblings.removeClass('open');
                    mphome.clearUlHeight($siblings.children('ul'));
                }
            }
        });

        // 响应菜单项
        mphome.components.nav.delegate('li:not(.has-sub-menu) > a', 'click', function (e) {
            var $a = $(this);
            var $li = $a.parent();
            if ($li.hasClass('active')) {
                return;
            }
            // 查看类型
            var type = $a.attr('type');
            if (type == 'action') {
                mphome.nav.navAction($a);
                return;
            }
            else if (type == 'url') {
                mphome.nav.navUrl({
                    'url': $a.attr('url'),
                    'label': $a.attr('label'),
                    'args': $a.attr('args')
                });
            } else if (type == 'menu') {
                // 不应走到这里的
            } else {
                // TODO 还没实现其他
            }
            // 修改样式
            mphome.components.nav.find('li.active').removeClass('active');
            $li.addClass('active');

        });

        // 响应tab
        mphome.components.extTabGroup.delegate('li', 'click', function (e) {
            // 计算距离左边的left, 还有当前块的宽度
            var $li = $(this);
            if ($li.hasClass('active')) {
                return;
            }
            $li.siblings().removeClass('active');
            $li.addClass('active');
            mphome.components.extTabGroupBBar.css({
                'width': $li.attr('twidth') + 'px',
                'left': $li.attr('tleft') + 'px'
            });

            // 移动viewpage
            mphome.components.extTabViewpage.find('.ext-tab-container').removeClass('active');
            mphome.components.extTabViewpage.find('.ext-tab-container[ext=' + $li.attr('ext') + ']').addClass('active');
        });

        // 响应主fab
        mphome.components.mainFab.delegate('li.fab-button-mini', 'click', function (e) {
            var $li = $(this);
            var extNm = $li.attr('ext');
            var $ext = mphome.components.extOverlay.find('.ext-ol-container[ext=' + extNm + ']');
            // TODO
        });

        mphome.components.extOverlay.delegate('.ext-ol-container-colse', 'click', function (e) {
            mphome.components.extOverlayLoading.removeClass('open');
            var $ext = mphome.components.extOverlay.find('.ext-ol-container.show');
            // TODO
        })

    }

    mphome.regAction = function (acNm, acFun) {
        mphome.action[acNm] = acFun;
    }

    // 注册action
    for (ac in hconf.actions) {
        mphome.regAction(ac, hconf.actions[ac]);
    }

    // 开始监控事件
    mphome.startListen();
    mphome.ajaxloading.enable();

    // 加载用户信息
    mphome.user.loadInfo(function () {

        mphome.sysTip.tip('加载用户信息');

        // 加载扩展组件
        mphome.ext.loadTab(function () {

            mphome.sysTip.tip('加载扩展组件');

            // 默认选中第一个
            var $ftab = mphome.components.extTabGroup.children('li').first();
            if ($ftab.length > 0) {
                $ftab.click();
            } else {
                mphome.components.extSwtich.remove();
                mphome.components.extBtnGroup.remove();
                mphome.components.ext.remove();
            }

            // FIXME 在ios下的safari中, 不隐藏ext, 会有横向滑动
            mphome.components.ext.hide();
            mphome.components.extOverlay.hide();

            // 加载菜单
            mphome.nav.loadMenu(function () {

                mphome.sysTip.tip('加载菜单');

                // 默认打开userMenu
                if (mphome.components.userMenu.children().length > 0) {
                    mphome.userMenu.open();
                }

                // 初始化, 判断当前url
                var $a = null;
                var cui = location.href.indexOf('#');
                var args = null;
                if (cui != -1) {
                    var url = window.location.href.substr(cui + 1);
                    var qi = url.indexOf('?');
                    // 带着参数吗
                    if (qi != -1) {
                        args = url.substr(qi + 1);
                        url = url.substr(0, qi);
                    }
                    $a = $('a[url="' + url + '"]');
                    if ($a.length > 0) {
                        mphome.nav.navUrl({
                            'url': url,
                            'label': $a.attr('label'),
                            'args': args
                        });
                    }
                } else {
                    $a = mphome.components.mainMenu.find('li:not(.has-sub-menu) > a').first();
                    $a.click();
                }

                // 如果是子菜单中的, 打开菜单
                var $li = $a.parents('li.has-sub-menu');
                if ($li.length > 0) {
                    $li.addClass('open');
                    mphome.setUlHeight($li.children('ul'));
                }

                mphome.sysTip.tip('进入系统');

                // 显示桌面
                if (hconf.closeQuick) {
                    mphome.sysLoading.closeQuick();
                } else {
                    mphome.sysLoading.close();
                }
            });
        });
    });

    window.$mp = window.$mp || {};
    window.$mp.home = mphome;

});
