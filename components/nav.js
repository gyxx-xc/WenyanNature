/**
 * WenyanNature 共享导航组件
 * 根据当前页面路径自适应生成 Header、Tabs 和 Skip 导航。
 *
 * 原理：
 * 每种栏目（首页 / 文言语法 / 游戏内容 / 模块索引 / 参与贡献）的页面都引用相同的
 * 导航 HTML 片段。组件根据当前 URL，自动添加返回上一级目录的前缀，使所有链接路径
 * 始终指向正确的位置。
 *
 * 使用方式：
 * 在每个页面的 </body> 之前添加一行：
 *   <script src="components/nav.js"></script>
 */
(function () {
  // ---- 1. 计算当前页面相对于仓库根目录的深度 ----
  var scope = window.__md_scope;
  var scopePath = scope
    ? scope.pathname.replace(/\/$/, '')       // 去掉末尾 '/'
    : '/WenyanNature';
  var pagePath = location.pathname.replace(/\/$/, '');

  // 把路径拆成段落，例如 ['WenyanNature', 'usage', 'quick_start']
  var scopeParts = scopePath.split('/').filter(Boolean);
  var pageParts = pagePath.split('/').filter(Boolean);

  // 当前页面比 scope 根深几层
  var relDepth = pageParts.length - scopeParts.length;
  if (relDepth < 0) relDepth = 0;

  // 生成相对引用前缀：0 层 → '.'，1 层 → '../'，2 层 → '../../'
  var rel = relDepth === 0 ? '.' : new Array(relDepth + 1).join('../');

  // ---- 2. 判断当前处于哪个顶级栏目（用于 Tabs 高亮） ----
  function getActiveTab() {
    var full = pageParts.join('/');
    if (full === scopeParts.join('/')) return 'home';
    if (full.indexOf('/usage') >= 0) return 'usage';
    if (full.indexOf('/in_game') >= 0) return 'in_game';
    if (full.indexOf('/modules') >= 0) return 'modules';
    if (full.indexOf('/development') >= 0) return 'development';
    return 'home';
  }

  var activeTab = getActiveTab();

  // ========== Skip to content ==========
  var skipHTML = '<div data-md-component="skip">'
    + '<a href="#main-content" class="md-skip">Skip to content</a>'
    + '</div>';

  // ========== Header ==========
  var headerHTML = '<header class="md-header" data-md-component="header">'
    + '<nav class="md-header__inner md-grid" aria-label="Header">'

    // Logo
    + '<a href="' + rel + '/" title="吾有一術" class="md-header__button md-logo" aria-label="吾有一術" data-md-component="logo">'
    + '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">'
    + '<path d="M12 8a3 3 0 0 0 3-3 3 3 0 0 0-3-3 3 3 0 0 0-3 3 3 3 0 0 0 3 3m0 3.54C9.64 9.35 6.5 8 3 8v11c3.5 0 6.64 1.35 9 3.54 2.36-2.19 5.5-3.54 9-3.54V8c-3.5 0-6.64 1.35-9 3.54"/>'
    + '</svg>'
    + '</a>'

    // Menu toggle button
    + '<label class="md-header__button md-icon" for="__drawer">'
    + '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">'
    + '<path d="M3 6h18v2H3zm0 5h18v2H3zm0 5h18v2H3z"/>'
    + '</svg>'
    + '</label>'

    // Title
    + '<div class="md-header__title" data-md-component="header-title">'
    + '<div class="md-header__ellipsis">'
    + '<div class="md-header__topic"><span class="md-ellipsis">吾有一術</span></div>'
    + '<div class="md-header__topic" data-md-component="header-topic"><span class="md-ellipsis"></span></div>'
    + '</div></div>'

    // Palette (color scheme) toggle
    + '<form class="md-header__option" data-md-component="palette">'
    + '<input class="md-option" data-md-color-media="(prefers-color-scheme)" data-md-color-scheme="default" data-md-color-primary="indigo" data-md-color-accent="indigo" aria-label="Switch to light mode" type="radio" name="__palette" id="__palette_0">'
    + '<label class="md-header__button md-icon" title="Switch to light mode" for="__palette_1" hidden>'
    + '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="m14.3 16-.7-2h-3.2l-.7 2H7.8L11 7h2l3.2 9zM20 8.69V4h-4.69L12 .69 8.69 4H4v4.69L.69 12 4 15.31V20h4.69L12 23.31 15.31 20H20v-4.69L23.31 12zm-9.15 3.96h2.3L12 9z"/></svg>'
    + '</label>'
    + '<input class="md-option" data-md-color-media="(prefers-color-scheme: light)" data-md-color-scheme="default" data-md-color-primary="indigo" data-md-color-accent="indigo" aria-label="Switch to dark mode" type="radio" name="__palette" id="__palette_1">'
    + '<label class="md-header__button md-icon" title="Switch to dark mode" for="__palette_2" hidden>'
    + '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M12 8a4 4 0 0 0-4 4 4 4 0 0 0 4 4 4 4 0 0 0 4-4 4 4 0 0 0-4-4m0 10a6 6 0 0 1-6-6 6 6 0 0 1 6-6 6 6 0 0 1 6 6 6 6 0 0 1-6 6m8-9.31V4h-4.69L12 .69 8.69 4H4v4.69L.69 12 4 15.31V20h4.69L12 23.31 15.31 20H20v-4.69L23.31 12z"/></svg>'
    + '</label>'
    + '<input class="md-option" data-md-color-media="(prefers-color-scheme: dark)" data-md-color-scheme="slate" data-md-color-primary="indigo" data-md-color-accent="indigo" aria-label="Switch to system preference" type="radio" name="__palette" id="__palette_2">'
    + '<label class="md-header__button md-icon" title="Switch to system preference" for="__palette_0" hidden>'
    + '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M12 18c-.89 0-1.74-.2-2.5-.55C11.56 16.5 13 14.42 13 12s-1.44-4.5-3.5-5.45C10.26 6.2 11.11 6 12 6a6 6 0 0 1 6 6 6 6 0 0 1-6 6m8-9.31V4h-4.69L12 .69 8.69 4H4v4.69L.69 12 4 15.31V20h4.69L12 23.31 15.31 20H20v-4.69L23.31 12z"/></svg>'
    + '</label>'
    + '</form>'

    // Search toggle
    + '<label class="md-header__button md-icon" for="__search">'
    + '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M9.5 3A6.5 6.5 0 0 1 16 9.5c0 1.61-.59 3.09-1.56 4.23l.27.27h.79l5 5-1.5 1.5-5-5v-.79l-.27-.27A6.52 6.52 0 0 1 9.5 16 6.5 6.5 0 0 1 3 9.5 6.5 6.5 0 0 1 9.5 3m0 2C7 5 5 7 5 9.5S7 14 9.5 14 14 12 14 9.5 12 5 9.5 5"/></svg>'
    + '</label>'

    // Search box
    + '<div class="md-search" data-md-component="search" role="dialog">'
    + '<label class="md-search__overlay" for="__search"></label>'
    + '<div class="md-search__inner" role="search">'
    + '<form class="md-search__form" name="search">'
    + '<input type="text" class="md-search__input" name="query" aria-label="Search" placeholder="Search" autocapitalize="off" autocorrect="off" autocomplete="off" spellcheck="false" data-md-component="search-query" required>'
    + '<label class="md-search__icon md-icon" for="__search">'
    + '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M9.5 3A6.5 6.5 0 0 1 16 9.5c0 1.61-.59 3.09-1.56 4.23l.27.27h.79l5 5-1.5 1.5-5-5v-.79l-.27-.27A6.52 6.52 0 0 1 9.5 16 6.5 6.5 0 0 1 3 9.5 6.5 6.5 0 0 1 9.5 3m0 2C7 5 5 7 5 9.5S7 14 9.5 14 14 12 14 9.5 12 5 9.5 5"/></svg>'
    + '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M20 11v2H8l5.5 5.5-1.42 1.42L4.16 12l7.92-7.92L13.5 5.5 8 11z"/></svg>'
    + '</label>'
    + '<nav class="md-search__options" aria-label="Search">'
    + '<button type="reset" class="md-search__icon md-icon" title="Clear" aria-label="Clear" tabindex="-1">'
    + '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M19 6.41 17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/></svg>'
    + '</button>'
    + '</nav>'
    + '</form>'
    + '<div class="md-search__output">'
    + '<div class="md-search__scrollwrap" tabindex="0" data-md-scrollfix>'
    + '<div class="md-search-result" data-md-component="search-result">'
    + '<div class="md-search-result__meta">Initializing search</div>'
    + '<ol class="md-search-result__list" role="presentation"></ol>'
    + '</div></div></div>'
    + '</div></div>'

    + '</nav></header>';

  // ========== Palette (color scheme) HTML ==========
  // 三个按钮分别对应：自动(系统偏好)、亮色模式、暗色模式
  // 使用 data-md-color-media 属性区分
  var PALETTE = {
    AUTO:  { id: '__palette_0', media: '',              scheme: 'default', nextId: '__palette_1', label: 'Switch to light mode' },
    LIGHT: { id: '__palette_1', media: '(prefers-color-scheme: light)', scheme: 'default', nextId: '__palette_2', label: 'Switch to dark mode' },
    DARK:  { id: '__palette_2', media: '(prefers-color-scheme: dark)',  scheme: 'slate',   nextId: '__palette_0', label: 'Switch to system preference' }
  };

  // ========== Tabs ==========
  var tabsHTML = '<nav class="md-tabs" aria-label="Tabs" data-md-component="tabs">'
    + '<div class="md-grid"><ul class="md-tabs__list">'

    + '<li class="md-tabs__item' + (activeTab === 'home' ? ' md-tabs__item--active' : '') + '">'
    + '<a href="' + rel + '/" class="md-tabs__link">吾有一术</a></li>'

    + '<li class="md-tabs__item' + (activeTab === 'usage' ? ' md-tabs__item--active' : '') + '">'
    + '<a href="' + rel + '/usage/" class="md-tabs__link">文言语法</a></li>'

    + '<li class="md-tabs__item' + (activeTab === 'in_game' ? ' md-tabs__item--active' : '') + '">'
    + '<a href="' + rel + '/in_game/crafting_block/" class="md-tabs__link">游戏内容</a></li>'

    + '<li class="md-tabs__item' + (activeTab === 'modules' ? ' md-tabs__item--active' : '') + '">'
    + '<a href="' + rel + '/modules/bit/" class="md-tabs__link">模块索引</a></li>'

    + '<li class="md-tabs__item' + (activeTab === 'development' ? ' md-tabs__item--active' : '') + '">'
    + '<a href="' + rel + '/development/structure/" class="md-tabs__link">参与贡献</a></li>'

    + '</ul></div></nav>';

  // ========== Inject into DOM ==========
  // 插入 Skip link（放在 body 最前面）
  var skipDiv = document.createElement('div');
  skipDiv.innerHTML = skipHTML;
  document.body.insertBefore(skipDiv.firstElementChild, document.body.firstChild);

  // 插入 Header
  var headerDiv = document.createElement('div');
  headerDiv.innerHTML = headerHTML;
  var overlay = document.querySelector('.md-overlay');
  if (overlay) {
    overlay.parentNode.insertBefore(headerDiv.firstElementChild, overlay);
  } else {
    document.body.insertBefore(headerDiv.firstElementChild, document.body.firstChild);
  }

  // ========== Palette initialization ==========
  function initPalette() {
    var saved = __md_get('__palette');
    var currentId;

    if (saved && saved.color && saved.color.media !== undefined) {
      var media = saved.color.media;
      if (media === '(prefers-color-scheme)') {
        // 自动模式：根据系统偏好解析为具体的亮色/暗色方案，再保存
        var sysQuery = matchMedia('(prefers-color-scheme: light)');
        if (sysQuery.matches) {
          media = '(prefers-color-scheme: light)';
        } else {
          media = '(prefers-color-scheme: dark)';
        }
        // 更新保存状态
        __md_set('__palette', { color: { media: media, scheme: media === '(prefers-color-scheme: light)' ? 'default' : 'slate', primary: 'default', accent: 'default' } });
        saved = __md_get('__palette');
      }

      if (media === '(prefers-color-scheme: light)') {
        currentId = PALETTE.LIGHT.id;
      } else if (media === '(prefers-color-scheme: dark)') {
        currentId = PALETTE.DARK.id;
      } else {
        currentId = PALETTE.AUTO.id;
      }
    } else {
      // 默认使用自动模式
      currentId = PALETTE.AUTO.id;
    }

    // 找到当前应该激活的 input
    var activeInput = document.getElementById(currentId);
    if (activeInput) {
      activeInput.checked = true;
      applyScheme(activeInput);
    }

    // 显示对应用户可见的按钮（隐藏当前模式对应的 label，显示下一个可切换到的 label）
    updateVisibleButton(currentId);
  }

  function applyScheme(input) {
    var scheme = input.getAttribute('data-md-color-scheme');
    var primary = input.getAttribute('data-md-color-primary');
    var accent = input.getAttribute('data-md-color-accent');
    var media = input.getAttribute('data-md-color-media');

    document.body.setAttribute('data-md-color-scheme', scheme);
    document.body.setAttribute('data-md-color-primary', primary);
    document.body.setAttribute('data-md-color-accent', accent);

    __md_set('__palette', {
      color: {
        media: media,
        scheme: scheme,
        primary: primary,
        accent: accent
      }
    });
  }

  function updateVisibleButton(currentId) {
    // 找到当前激活的 palette entry
    var currentEntry = null;
    for (var key in PALETTE) {
      if (PALETTE[key].id === currentId) {
        currentEntry = PALETTE[key];
        break;
      }
    }
    if (!currentEntry) return;

    var nextId = currentEntry.nextId;

    // 隐藏所有 label
    var allLabels = document.querySelectorAll('.md-header__option label');
    for (var i = 0; i < allLabels.length; i++) {
      allLabels[i].setAttribute('hidden', '');
    }

    // 显示下一个可切换到的按钮的 label
    var nextLabel = document.querySelector('label[for="' + nextId + '"]');
    if (nextLabel) {
      nextLabel.removeAttribute('hidden');
    }
  }

  // 绑定 palette 切换事件
  function bindPaletteEvents() {
    var inputs = document.querySelectorAll('.md-header__option input[name="__palette"]');
    for (var i = 0; i < inputs.length; i++) {
      inputs[i].addEventListener('change', function() {
        if (this.checked) {
          applyScheme(this);
          updateVisibleButton(this.id);
        }
      });
    }
  }

  // 插入 Tabs（放在 .md-container 最前面）
  var container = document.querySelector('.md-container');
  if (container) {
    var tabsDiv = document.createElement('div');
    tabsDiv.innerHTML = tabsHTML;
    container.insertBefore(tabsDiv.firstElementChild, container.firstChild);
  }

  // 初始化 palette 并绑定事件
  initPalette();
  bindPaletteEvents();

})();
