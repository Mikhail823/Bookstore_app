'use strict';
(function ($) {
    var px = '';

    function $x(selector) {
        return $(x(selector));
    }

    function x(selector) {
        var arraySelectors = selector.split('.'), firstNotClass = !!arraySelectors[0];
        selector = '';
        for (var i = 0; i < arraySelectors.length; i++) {
            if (!i) {
                if (firstNotClass) selector += arraySelectors[i];
                continue;
            }
            selector += '.' + px + arraySelectors[i];
        }
        return selector;
    }

    $(function () {
        var button = function () {
            return {
                init: function () {
                    $('.Site').on('click', 'a.btn[disabled="disabled"]', function (e) {
                        e.preventDefault();
                    });
                }
            };
        };
        button().init();
        ;(function ($) {
            "use strict";
            var feature = {};
            feature.fileapi = $("<input type='file'/>").get(0).files !== undefined;
            feature.formdata = window.FormData !== undefined;
            var hasProp = !!$.fn.prop;
            $.fn.attr2 = function () {
                if (!hasProp)
                    return this.attr.apply(this, arguments);
                var val = this.prop.apply(this, arguments);
                if ((val && val.jquery) || typeof val === 'string')
                    return val;
                return this.attr.apply(this, arguments);
            };
            $.fn.ajaxSubmit = function (options) {
                if (!this.length) {
                    log('ajaxSubmit: skipping submit process - no element selected');
                    return this;
                }
                var method, action, url, $form = this;
                if (typeof options == 'function') {
                    options = {success: options};
                } else if (options === undefined) {
                    options = {};
                }
                method = options.type || this.attr2('method');
                action = options.url || this.attr2('action');
                url = (typeof action === 'string') ? $.trim(action) : '';
                url = url || window.location.href || '';
                if (url) {
                    url = (url.match(/^([^#]+)/) || [])[1];
                }
                options = $.extend(true, {
                    url: url,
                    success: $.ajaxSettings.success,
                    type: method || 'GET',
                    iframeSrc: /^https/i.test(window.location.href || '') ? 'javascript:false' : 'about:blank'
                }, options);
                var veto = {};
                this.trigger('form-pre-serialize', [this, options, veto]);
                if (veto.veto) {
                    log('ajaxSubmit: submit vetoed via form-pre-serialize trigger');
                    return this;
                }
                if (options.beforeSerialize && options.beforeSerialize(this, options) === false) {
                    log('ajaxSubmit: submit aborted via beforeSerialize callback');
                    return this;
                }
                var traditional = options.traditional;
                if (traditional === undefined) {
                    traditional = $.ajaxSettings.traditional;
                }
                var elements = [];
                var qx, a = this.formToArray(options.semantic, elements);
                if (options.data) {
                    options.extraData = options.data;
                    qx = $.param(options.data, traditional);
                }
                if (options.beforeSubmit && options.beforeSubmit(a, this, options) === false) {
                    log('ajaxSubmit: submit aborted via beforeSubmit callback');
                    return this;
                }
                this.trigger('form-submit-validate', [a, this, options, veto]);
                if (veto.veto) {
                    log('ajaxSubmit: submit vetoed via form-submit-validate trigger');
                    return this;
                }
                var q = $.param(a, traditional);
                if (qx) {
                    q = (q ? (q + '&' + qx) : qx);
                }
                if (options.type.toUpperCase() == 'GET') {
                    options.url += (options.url.indexOf('?') >= 0 ? '&' : '?') + q;
                    options.data = null;
                } else {
                    options.data = q;
                }
                var callbacks = [];
                if (options.resetForm) {
                    callbacks.push(function () {
                        $form.resetForm();
                    });
                }
                if (options.clearForm) {
                    callbacks.push(function () {
                        $form.clearForm(options.includeHidden);
                    });
                }
                if (!options.dataType && options.target) {
                    var oldSuccess = options.success || function () {
                    };
                    callbacks.push(function (data) {
                        var fn = options.replaceTarget ? 'replaceWith' : 'html';
                        $(options.target)[fn](data).each(oldSuccess, arguments);
                    });
                } else if (options.success) {
                    callbacks.push(options.success);
                }
                options.success = function (data, status, xhr) {
                    var context = options.context || this;
                    for (var i = 0, max = callbacks.length; i < max; i++) {
                        callbacks[i].apply(context, [data, status, xhr || $form, $form]);
                    }
                };
                if (options.error) {
                    var oldError = options.error;
                    options.error = function (xhr, status, error) {
                        var context = options.context || this;
                        oldError.apply(context, [xhr, status, error, $form]);
                    };
                }
                if (options.complete) {
                    var oldComplete = options.complete;
                    options.complete = function (xhr, status) {
                        var context = options.context || this;
                        oldComplete.apply(context, [xhr, status, $form]);
                    };
                }
                var fileInputs = $('input[type=file]:enabled[value!=""]', this);
                var hasFileInputs = fileInputs.length > 0;
                var mp = 'multipart/form-data';
                var multipart = ($form.attr('enctype') == mp || $form.attr('encoding') == mp);
                var fileAPI = feature.fileapi && feature.formdata;
                log("fileAPI :" + fileAPI);
                var shouldUseFrame = (hasFileInputs || multipart) && !fileAPI;
                var jqxhr;
                if (options.iframe !== false && (options.iframe || shouldUseFrame)) {
                    if (options.closeKeepAlive) {
                        $.get(options.closeKeepAlive, function () {
                            jqxhr = fileUploadIframe(a);
                        });
                    } else {
                        jqxhr = fileUploadIframe(a);
                    }
                } else if ((hasFileInputs || multipart) && fileAPI) {
                    jqxhr = fileUploadXhr(a);
                } else {
                    jqxhr = $.ajax(options);
                }
                $form.removeData('jqxhr').data('jqxhr', jqxhr);
                for (var k = 0; k < elements.length; k++)
                    elements[k] = null;
                this.trigger('form-submit-notify', [this, options]);
                return this;

                function deepSerialize(extraData) {
                    var serialized = $.param(extraData, options.traditional).split('&');
                    var len = serialized.length;
                    var result = [];
                    var i, part;
                    for (i = 0; i < len; i++) {
                        serialized[i] = serialized[i].replace(/\+/g, ' ');
                        part = serialized[i].split('=');
                        result.push([decodeURIComponent(part[0]), decodeURIComponent(part[1])]);
                    }
                    return result;
                }

                function fileUploadXhr(a) {
                    var formdata = new FormData();
                    for (var i = 0; i < a.length; i++) {
                        formdata.append(a[i].name, a[i].value);
                    }
                    if (options.extraData) {
                        var serializedData = deepSerialize(options.extraData);
                        for (i = 0; i < serializedData.length; i++)
                            if (serializedData[i])
                                formdata.append(serializedData[i][0], serializedData[i][1]);
                    }
                    options.data = null;
                    var s = $.extend(true, {}, $.ajaxSettings, options, {
                        contentType: false,
                        processData: false,
                        cache: false,
                        type: method || 'POST'
                    });
                    if (options.uploadProgress) {
                        s.xhr = function () {
                            var xhr = $.ajaxSettings.xhr();
                            if (xhr.upload) {
                                xhr.upload.addEventListener('progress', function (event) {
                                    var percent = 0;
                                    var position = event.loaded || event.position;
                                    var total = event.total;
                                    if (event.lengthComputable) {
                                        percent = Math.ceil(position / total * 100);
                                    }
                                    options.uploadProgress(event, position, total, percent);
                                }, false);
                            }
                            return xhr;
                        };
                    }
                    s.data = null;
                    var beforeSend = s.beforeSend;
                    s.beforeSend = function (xhr, o) {
                        o.data = formdata;
                        if (beforeSend)
                            beforeSend.call(this, xhr, o);
                    };
                    return $.ajax(s);
                }

                function fileUploadIframe(a) {
                    var form = $form[0], el, i, s, g, id, $io, io, xhr, sub, n, timedOut, timeoutHandle;
                    var deferred = $.Deferred();
                    if (a) {
                        for (i = 0; i < elements.length; i++) {
                            el = $(elements[i]);
                            if (hasProp)
                                el.prop('disabled', false); else
                                el.removeAttr('disabled');
                        }
                    }
                    s = $.extend(true, {}, $.ajaxSettings, options);
                    s.context = s.context || s;
                    id = 'jqFormIO' + (new Date().getTime());
                    if (s.iframeTarget) {
                        $io = $(s.iframeTarget);
                        n = $io.attr2('name');
                        if (!n)
                            $io.attr2('name', id); else
                            id = n;
                    } else {
                        $io = $('<iframe name="' + id + '" src="' + s.iframeSrc + '" />');
                        $io.css({position: 'absolute', top: '-1000px', left: '-1000px'});
                    }
                    io = $io[0];
                    xhr = {
                        aborted: 0,
                        responseText: null,
                        responseXML: null,
                        status: 0,
                        statusText: 'n/a',
                        getAllResponseHeaders: function () {
                        },
                        getResponseHeader: function () {
                        },
                        setRequestHeader: function () {
                        },
                        abort: function (status) {
                            var e = (status === 'timeout' ? 'timeout' : 'aborted');
                            log('aborting upload... ' + e);
                            this.aborted = 1;
                            try {
                                if (io.contentWindow.document.execCommand) {
                                    io.contentWindow.document.execCommand('Stop');
                                }
                            } catch (ignore) {
                            }
                            $io.attr('src', s.iframeSrc);
                            xhr.error = e;
                            if (s.error)
                                s.error.call(s.context, xhr, e, status);
                            if (g)
                                $.event.trigger("ajaxError", [xhr, s, e]);
                            if (s.complete)
                                s.complete.call(s.context, xhr, e);
                        }
                    };
                    g = s.global;
                    if (g && 0 === $.active++) {
                        $.event.trigger("ajaxStart");
                    }
                    if (g) {
                        $.event.trigger("ajaxSend", [xhr, s]);
                    }
                    if (s.beforeSend && s.beforeSend.call(s.context, xhr, s) === false) {
                        if (s.global) {
                            $.active--;
                        }
                        deferred.reject();
                        return deferred;
                    }
                    if (xhr.aborted) {
                        deferred.reject();
                        return deferred;
                    }
                    sub = form.clk;
                    if (sub) {
                        n = sub.name;
                        if (n && !sub.disabled) {
                            s.extraData = s.extraData || {};
                            s.extraData[n] = sub.value;
                            if (sub.type == "image") {
                                s.extraData[n + '.x'] = form.clk_x;
                                s.extraData[n + '.y'] = form.clk_y;
                            }
                        }
                    }
                    var CLIENT_TIMEOUT_ABORT = 1;
                    var SERVER_ABORT = 2;

                    function getDoc(frame) {
                        var doc = null;
                        try {
                            if (frame.contentWindow) {
                                doc = frame.contentWindow.document;
                            }
                        } catch (err) {
                            log('cannot get iframe.contentWindow document: ' + err);
                        }
                        if (doc) {
                            return doc;
                        }
                        try {
                            doc = frame.contentDocument ? frame.contentDocument : frame.document;
                        } catch (err) {
                            log('cannot get iframe.contentDocument: ' + err);
                            doc = frame.document;
                        }
                        return doc;
                    }

                    var csrf_token = $('meta[name=csrf-token]').attr('content');
                    var csrf_param = $('meta[name=csrf-param]').attr('content');
                    if (csrf_param && csrf_token) {
                        s.extraData = s.extraData || {};
                        s.extraData[csrf_param] = csrf_token;
                    }

                    function doSubmit() {
                        var t = $form.attr2('target'), a = $form.attr2('action');
                        form.setAttribute('target', id);
                        if (!method) {
                            form.setAttribute('method', 'POST');
                        }
                        if (a != s.url) {
                            form.setAttribute('action', s.url);
                        }
                        if (!s.skipEncodingOverride && (!method || /post/i.test(method))) {
                            $form.attr({encoding: 'multipart/form-data', enctype: 'multipart/form-data'});
                        }
                        if (s.timeout) {
                            timeoutHandle = setTimeout(function () {
                                timedOut = true;
                                cb(CLIENT_TIMEOUT_ABORT);
                            }, s.timeout);
                        }

                        function checkState() {
                            try {
                                var state = getDoc(io).readyState;
                                log('state = ' + state);
                                if (state && state.toLowerCase() == 'uninitialized')
                                    setTimeout(checkState, 50);
                            } catch (e) {
                                log('Server abort: ', e, ' (', e.name, ')');
                                cb(SERVER_ABORT);
                                if (timeoutHandle)
                                    clearTimeout(timeoutHandle);
                                timeoutHandle = undefined;
                            }
                        }

                        var extraInputs = [];
                        try {
                            if (s.extraData) {
                                for (var n in s.extraData) {
                                    if (s.extraData.hasOwnProperty(n)) {
                                        if ($.isPlainObject(s.extraData[n]) && s.extraData[n].hasOwnProperty('name') && s.extraData[n].hasOwnProperty('value')) {
                                            extraInputs.push($('<input type="hidden" name="' + s.extraData[n].name + '">').val(s.extraData[n].value).appendTo(form)[0]);
                                        } else {
                                            extraInputs.push($('<input type="hidden" name="' + n + '">').val(s.extraData[n]).appendTo(form)[0]);
                                        }
                                    }
                                }
                            }
                            if (!s.iframeTarget) {
                                $io.appendTo('body');
                                if (io.attachEvent)
                                    io.attachEvent('onload', cb); else
                                    io.addEventListener('load', cb, false);
                            }
                            setTimeout(checkState, 15);
                            try {
                                form.submit();
                            } catch (err) {
                                var submitFn = document.createElement('form').submit;
                                submitFn.apply(form);
                            }
                        } finally {
                            form.setAttribute('action', a);
                            if (t) {
                                form.setAttribute('target', t);
                            } else {
                                $form.removeAttr('target');
                            }
                            $(extraInputs).remove();
                        }
                    }

                    if (s.forceSync) {
                        doSubmit();
                    } else {
                        setTimeout(doSubmit, 10);
                    }
                    var data, doc, domCheckCount = 50, callbackProcessed;

                    function cb(e) {
                        if (xhr.aborted || callbackProcessed) {
                            return;
                        }
                        doc = getDoc(io);
                        if (!doc) {
                            log('cannot access response document');
                            e = SERVER_ABORT;
                        }
                        if (e === CLIENT_TIMEOUT_ABORT && xhr) {
                            xhr.abort('timeout');
                            deferred.reject(xhr, 'timeout');
                            return;
                        } else if (e == SERVER_ABORT && xhr) {
                            xhr.abort('server abort');
                            deferred.reject(xhr, 'error', 'server abort');
                            return;
                        }
                        if (!doc || doc.location.href == s.iframeSrc) {
                            if (!timedOut)
                                return;
                        }
                        if (io.detachEvent)
                            io.detachEvent('onload', cb); else
                            io.removeEventListener('load', cb, false);
                        var status = 'success', errMsg;
                        try {
                            if (timedOut) {
                                throw'timeout';
                            }
                            var isXml = s.dataType == 'xml' || doc.XMLDocument || $.isXMLDoc(doc);
                            log('isXml=' + isXml);
                            if (!isXml && window.opera && (doc.body === null || !doc.body.innerHTML)) {
                                if (--domCheckCount) {
                                    log('requeing onLoad callback, DOM not available');
                                    setTimeout(cb, 250);
                                    return;
                                }
                            }
                            var docRoot = doc.body ? doc.body : doc.documentElement;
                            xhr.responseText = docRoot ? docRoot.innerHTML : null;
                            xhr.responseXML = doc.XMLDocument ? doc.XMLDocument : doc;
                            if (isXml)
                                s.dataType = 'xml';
                            xhr.getResponseHeader = function (header) {
                                var headers = {'content-type': s.dataType};
                                return headers[header];
                            };
                            if (docRoot) {
                                xhr.status = Number(docRoot.getAttribute('status')) || xhr.status;
                                xhr.statusText = docRoot.getAttribute('statusText') || xhr.statusText;
                            }
                            var dt = (s.dataType || '').toLowerCase();
                            var scr = /(json|script|text)/.test(dt);
                            if (scr || s.textarea) {
                                var ta = doc.getElementsByTagName('textarea')[0];
                                if (ta) {
                                    xhr.responseText = ta.value;
                                    xhr.status = Number(ta.getAttribute('status')) || xhr.status;
                                    xhr.statusText = ta.getAttribute('statusText') || xhr.statusText;
                                } else if (scr) {
                                    var pre = doc.getElementsByTagName('pre')[0];
                                    var b = doc.getElementsByTagName('body')[0];
                                    if (pre) {
                                        xhr.responseText = pre.textContent ? pre.textContent : pre.innerText;
                                    } else if (b) {
                                        xhr.responseText = b.textContent ? b.textContent : b.innerText;
                                    }
                                }
                            } else if (dt == 'xml' && !xhr.responseXML && xhr.responseText) {
                                xhr.responseXML = toXml(xhr.responseText);
                            }
                            try {
                                data = httpData(xhr, dt, s);
                            } catch (err) {
                                status = 'parsererror';
                                xhr.error = errMsg = (err || status);
                            }
                        } catch (err) {
                            log('error caught: ', err);
                            status = 'error';
                            xhr.error = errMsg = (err || status);
                        }
                        if (xhr.aborted) {
                            log('upload aborted');
                            status = null;
                        }
                        if (xhr.status) {
                            status = (xhr.status >= 200 && xhr.status < 300 || xhr.status === 304) ? 'success' : 'error';
                        }
                        if (status === 'success') {
                            if (s.success)
                                s.success.call(s.context, data, 'success', xhr);
                            deferred.resolve(xhr.responseText, 'success', xhr);
                            if (g)
                                $.event.trigger("ajaxSuccess", [xhr, s]);
                        } else if (status) {
                            if (errMsg === undefined)
                                errMsg = xhr.statusText;
                            if (s.error)
                                s.error.call(s.context, xhr, status, errMsg);
                            deferred.reject(xhr, 'error', errMsg);
                            if (g)
                                $.event.trigger("ajaxError", [xhr, s, errMsg]);
                        }
                        if (g)
                            $.event.trigger("ajaxComplete", [xhr, s]);
                        if (g && !--$.active) {
                            $.event.trigger("ajaxStop");
                        }
                        if (s.complete)
                            s.complete.call(s.context, xhr, status);
                        callbackProcessed = true;
                        if (s.timeout)
                            clearTimeout(timeoutHandle);
                        setTimeout(function () {
                            if (!s.iframeTarget)
                                $io.remove();
                            xhr.responseXML = null;
                        }, 100);
                    }

                    var toXml = $.parseXML || function (s, doc) {
                        if (window.ActiveXObject) {
                            doc = new ActiveXObject('Microsoft.XMLDOM');
                            doc.async = 'false';
                            doc.loadXML(s);
                        } else {
                            doc = (new DOMParser()).parseFromString(s, 'text/xml');
                        }
                        return (doc && doc.documentElement && doc.documentElement.nodeName != 'parsererror') ? doc : null;
                    };
                    var parseJSON = $.parseJSON || function (s) {
                        return window['eval']('(' + s + ')');
                    };
                    var httpData = function (xhr, type, s) {
                        var ct = xhr.getResponseHeader('content-type') || '',
                            xml = type === 'xml' || !type && ct.indexOf('xml') >= 0,
                            data = xml ? xhr.responseXML : xhr.responseText;
                        if (xml && data.documentElement.nodeName === 'parsererror') {
                            if ($.error)
                                $.error('parsererror');
                        }
                        if (s && s.dataFilter) {
                            data = s.dataFilter(data, type);
                        }
                        if (typeof data === 'string') {
                            if (type === 'json' || !type && ct.indexOf('json') >= 0) {
                                data = parseJSON(data);
                            } else if (type === "script" || !type && ct.indexOf("javascript") >= 0) {
                                $.globalEval(data);
                            }
                        }
                        return data;
                    };
                    return deferred;
                }
            };
            $.fn.ajaxForm = function (options) {
                options = options || {};
                options.delegation = options.delegation && $.isFunction($.fn.on);
                if (!options.delegation && this.length === 0) {
                    var o = {s: this.selector, c: this.context};
                    if (!$.isReady && o.s) {
                        log('DOM not ready, queuing ajaxForm');
                        $(function () {
                            $(o.s, o.c).ajaxForm(options);
                        });
                        return this;
                    }
                    log('terminating; zero elements found by selector' + ($.isReady ? '' : ' (DOM not ready)'));
                    return this;
                }
                if (options.delegation) {
                    $(document).off('submit.form-plugin', this.selector, doAjaxSubmit).off('click.form-plugin', this.selector, captureSubmittingElement).on('submit.form-plugin', this.selector, options, doAjaxSubmit).on('click.form-plugin', this.selector, options, captureSubmittingElement);
                    return this;
                }
                return this.ajaxFormUnbind().bind('submit.form-plugin', options, doAjaxSubmit).bind('click.form-plugin', options, captureSubmittingElement);
            };

            function doAjaxSubmit(e) {
                var options = e.data;
                if (!e.isDefaultPrevented()) {
                    e.preventDefault();
                    $(this).ajaxSubmit(options);
                }
            }

            function captureSubmittingElement(e) {
                var target = e.target;
                var $el = $(target);
                if (!($el.is("[type=submit],[type=image]"))) {
                    var t = $el.closest('[type=submit]');
                    if (t.length === 0) {
                        return;
                    }
                    target = t[0];
                }
                var form = this;
                form.clk = target;
                if (target.type == 'image') {
                    if (e.offsetX !== undefined) {
                        form.clk_x = e.offsetX;
                        form.clk_y = e.offsetY;
                    } else if (typeof $.fn.offset == 'function') {
                        var offset = $el.offset();
                        form.clk_x = e.pageX - offset.left;
                        form.clk_y = e.pageY - offset.top;
                    } else {
                        form.clk_x = e.pageX - target.offsetLeft;
                        form.clk_y = e.pageY - target.offsetTop;
                    }
                }
                setTimeout(function () {
                    form.clk = form.clk_x = form.clk_y = null;
                }, 100);
            }

            $.fn.ajaxFormUnbind = function () {
                return this.unbind('submit.form-plugin click.form-plugin');
            };
            $.fn.formToArray = function (semantic, elements) {
                var a = [];
                if (this.length === 0) {
                    return a;
                }
                var form = this[0];
                var els = semantic ? form.getElementsByTagName('*') : form.elements;
                if (!els) {
                    return a;
                }
                var i, j, n, v, el, max, jmax;
                for (i = 0, max = els.length; i < max; i++) {
                    el = els[i];
                    n = el.name;
                    if (!n || el.disabled) {
                        continue;
                    }
                    if (semantic && form.clk && el.type == "image") {
                        if (form.clk == el) {
                            a.push({name: n, value: $(el).val(), type: el.type});
                            a.push({name: n + '.x', value: form.clk_x}, {name: n + '.y', value: form.clk_y});
                        }
                        continue;
                    }
                    v = $.fieldValue(el, true);
                    if (v && v.constructor == Array) {
                        if (elements)
                            elements.push(el);
                        for (j = 0, jmax = v.length; j < jmax; j++) {
                            a.push({name: n, value: v[j]});
                        }
                    } else if (feature.fileapi && el.type == 'file') {
                        if (elements)
                            elements.push(el);
                        var files = el.files;
                        if (files.length) {
                            for (j = 0; j < files.length; j++) {
                                a.push({name: n, value: files[j], type: el.type});
                            }
                        } else {
                            a.push({name: n, value: '', type: el.type});
                        }
                    } else if (v !== null && typeof v != 'undefined') {
                        if (elements)
                            elements.push(el);
                        a.push({name: n, value: v, type: el.type, required: el.required});
                    }
                }
                if (!semantic && form.clk) {
                    var $input = $(form.clk), input = $input[0];
                    n = input.name;
                    if (n && !input.disabled && input.type == 'image') {
                        a.push({name: n, value: $input.val()});
                        a.push({name: n + '.x', value: form.clk_x}, {name: n + '.y', value: form.clk_y});
                    }
                }
                return a;
            };
            $.fn.formSerialize = function (semantic) {
                return $.param(this.formToArray(semantic));
            };
            $.fn.fieldSerialize = function (successful) {
                var a = [];
                this.each(function () {
                    var n = this.name;
                    if (!n) {
                        return;
                    }
                    var v = $.fieldValue(this, successful);
                    if (v && v.constructor == Array) {
                        for (var i = 0, max = v.length; i < max; i++) {
                            a.push({name: n, value: v[i]});
                        }
                    } else if (v !== null && typeof v != 'undefined') {
                        a.push({name: this.name, value: v});
                    }
                });
                return $.param(a);
            };
            $.fn.fieldValue = function (successful) {
                for (var val = [], i = 0, max = this.length; i < max; i++) {
                    var el = this[i];
                    var v = $.fieldValue(el, successful);
                    if (v === null || typeof v == 'undefined' || (v.constructor == Array && !v.length)) {
                        continue;
                    }
                    if (v.constructor == Array)
                        $.merge(val, v); else
                        val.push(v);
                }
                return val;
            };
            $.fieldValue = function (el, successful) {
                var n = el.name, t = el.type, tag = el.tagName.toLowerCase();
                if (successful === undefined) {
                    successful = true;
                }
                if (successful && (!n || el.disabled || t == 'reset' || t == 'button' || (t == 'checkbox' || t == 'radio') && !el.checked || (t == 'submit' || t == 'image') && el.form && el.form.clk != el || tag == 'select' && el.selectedIndex == -1)) {
                    return null;
                }
                if (tag == 'select') {
                    var index = el.selectedIndex;
                    if (index < 0) {
                        return null;
                    }
                    var a = [], ops = el.options;
                    var one = (t == 'select-one');
                    var max = (one ? index + 1 : ops.length);
                    for (var i = (one ? index : 0); i < max; i++) {
                        var op = ops[i];
                        if (op.selected) {
                            var v = op.value;
                            if (!v) {
                                v = (op.attributes && op.attributes['value'] && !(op.attributes['value'].specified)) ? op.text : op.value;
                            }
                            if (one) {
                                return v;
                            }
                            a.push(v);
                        }
                    }
                    return a;
                }
                return $(el).val();
            };
            $.fn.clearForm = function (includeHidden) {
                return this.each(function () {
                    $('input,select,textarea', this).clearFields(includeHidden);
                });
            };
            $.fn.clearFields = $.fn.clearInputs = function (includeHidden) {
                var re = /^(?:color|date|datetime|email|month|number|password|range|search|tel|text|time|url|week)$/i;
                return this.each(function () {
                    var t = this.type, tag = this.tagName.toLowerCase();
                    if (re.test(t) || tag == 'textarea') {
                        this.value = '';
                    } else if (t == 'checkbox' || t == 'radio') {
                        this.checked = false;
                    } else if (tag == 'select') {
                        this.selectedIndex = -1;
                    } else if (t == "file") {
                        if (/MSIE/.test(navigator.userAgent)) {
                            $(this).replaceWith($(this).clone(true));
                        } else {
                            $(this).val('');
                        }
                    } else if (includeHidden) {
                        if ((includeHidden === true && /hidden/.test(t)) || (typeof includeHidden == 'string' && $(this).is(includeHidden)))
                            this.value = '';
                    }
                });
            };
            $.fn.resetForm = function () {
                return this.each(function () {
                    if (typeof this.reset == 'function' || (typeof this.reset == 'object' && !this.reset.nodeType)) {
                        this.reset();
                    }
                });
            };
            $.fn.enable = function (b) {
                if (b === undefined) {
                    b = true;
                }
                return this.each(function () {
                    this.disabled = !b;
                });
            };
            $.fn.selected = function (select) {
                if (select === undefined) {
                    select = true;
                }
                return this.each(function () {
                    var t = this.type;
                    if (t == 'checkbox' || t == 'radio') {
                        this.checked = select;
                    } else if (this.tagName.toLowerCase() == 'option') {
                        var $sel = $(this).parent('select');
                        if (select && $sel[0] && $sel[0].type == 'select-one') {
                            $sel.find('option').selected(false);
                        }
                        this.selected = select;
                    }
                });
            };
            $.fn.ajaxSubmit.debug = false;

            function log() {
                if (!$.fn.ajaxSubmit.debug)
                    return;
                var msg = '[jquery.form] ' + Array.prototype.join.call(arguments, '');
                if (window.console && window.console.log) {
                    window.console.log(msg);
                } else if (window.opera && window.opera.postError) {
                    window.opera.postError(msg);
                }
            }
        })(jQuery);
        !function (a) {
            "function" == typeof define && define.amd ? define(["jquery"], a) : a("object" == typeof exports ? require("jquery") : jQuery)
        }(function (a) {
            var b, c = navigator.userAgent, d = /iphone/i.test(c), e = /chrome/i.test(c), f = /android/i.test(c);
            a.mask = {
                definitions: {9: "[0-9]", a: "[A-Za-z]", "*": "[A-Za-z0-9]"},
                autoclear: !0,
                dataName: "rawMaskFn",
                placeholder: "_"
            }, a.fn.extend({
                caret: function (a, b) {
                    var c;
                    if (0 !== this.length && !this.is(":hidden")) return "number" == typeof a ? (b = "number" == typeof b ? b : a, this.each(function () {
                        this.setSelectionRange ? this.setSelectionRange(a, b) : this.createTextRange && (c = this.createTextRange(), c.collapse(!0), c.moveEnd("character", b), c.moveStart("character", a), c.select())
                    })) : (this[0].setSelectionRange ? (a = this[0].selectionStart, b = this[0].selectionEnd) : document.selection && document.selection.createRange && (c = document.selection.createRange(), a = 0 - c.duplicate().moveStart("character", -1e5), b = a + c.text.length), {
                        begin: a,
                        end: b
                    })
                }, unmask: function () {
                    return this.trigger("unmask")
                }, mask: function (c, g) {
                    var h, i, j, k, l, m, n, o;
                    if (!c && this.length > 0) {
                        h = a(this[0]);
                        var p = h.data(a.mask.dataName);
                        return p ? p() : void 0
                    }
                    return g = a.extend({
                        autoclear: a.mask.autoclear,
                        placeholder: a.mask.placeholder,
                        completed: null
                    }, g), i = a.mask.definitions, j = [], k = n = c.length, l = null, a.each(c.split(""), function (a, b) {
                        "?" == b ? (n--, k = a) : i[b] ? (j.push(new RegExp(i[b])), null === l && (l = j.length - 1), k > a && (m = j.length - 1)) : j.push(null)
                    }), this.trigger("unmask").each(function () {
                        function h() {
                            if (g.completed) {
                                for (var a = l; m >= a; a++) if (j[a] && C[a] === p(a)) return;
                                g.completed.call(B)
                            }
                        }

                        function p(a) {
                            return g.placeholder.charAt(a < g.placeholder.length ? a : 0)
                        }

                        function q(a) {
                            for (; ++a < n && !j[a];) ;
                            return a
                        }

                        function r(a) {
                            for (; --a >= 0 && !j[a];) ;
                            return a
                        }

                        function s(a, b) {
                            var c, d;
                            if (!(0 > a)) {
                                for (c = a, d = q(b); n > c; c++) if (j[c]) {
                                    if (!(n > d && j[c].test(C[d]))) break;
                                    C[c] = C[d], C[d] = p(d), d = q(d)
                                }
                                z(), B.caret(Math.max(l, a))
                            }
                        }

                        function t(a) {
                            var b, c, d, e;
                            for (b = a, c = p(a); n > b; b++) if (j[b]) {
                                if (d = q(b), e = C[b], C[b] = c, !(n > d && j[d].test(e))) break;
                                c = e
                            }
                        }

                        function u() {
                            var a = B.val(), b = B.caret();
                            if (o && o.length && o.length > a.length) {
                                for (A(!0); b.begin > 0 && !j[b.begin - 1];) b.begin--;
                                if (0 === b.begin) for (; b.begin < l && !j[b.begin];) b.begin++;
                                B.caret(b.begin, b.begin)
                            } else {
                                for (A(!0); b.begin < n && !j[b.begin];) b.begin++;
                                B.caret(b.begin, b.begin)
                            }
                            h()
                        }

                        function v() {
                            A(), B.val() != E && B.change()
                        }

                        function w(a) {
                            if (!B.prop("readonly")) {
                                var b, c, e, f = a.which || a.keyCode;
                                o = B.val(), 8 === f || 46 === f || d && 127 === f ? (b = B.caret(), c = b.begin, e = b.end, e - c === 0 && (c = 46 !== f ? r(c) : e = q(c - 1), e = 46 === f ? q(e) : e), y(c, e), s(c, e - 1), a.preventDefault()) : 13 === f ? v.call(this, a) : 27 === f && (B.val(E), B.caret(0, A()), a.preventDefault())
                            }
                        }

                        function x(b) {
                            if (!B.prop("readonly")) {
                                var c, d, e, g = b.which || b.keyCode, i = B.caret();
                                if (!(b.ctrlKey || b.altKey || b.metaKey || 32 > g) && g && 13 !== g) {
                                    if (i.end - i.begin !== 0 && (y(i.begin, i.end), s(i.begin, i.end - 1)), c = q(i.begin - 1), n > c && (d = String.fromCharCode(g), j[c].test(d))) {
                                        if (t(c), C[c] = d, z(), e = q(c), f) {
                                            var k = function () {
                                                a.proxy(a.fn.caret, B, e)()
                                            };
                                            setTimeout(k, 0)
                                        } else B.caret(e);
                                        i.begin <= m && h()
                                    }
                                    b.preventDefault()
                                }
                            }
                        }

                        function y(a, b) {
                            var c;
                            for (c = a; b > c && n > c; c++) j[c] && (C[c] = p(c))
                        }

                        function z() {
                            B.val(C.join(""))
                        }

                        function A(a) {
                            var b, c, d, e = B.val(), f = -1;
                            for (b = 0, d = 0; n > b; b++) if (j[b]) {
                                for (C[b] = p(b); d++ < e.length;) if (c = e.charAt(d - 1), j[b].test(c)) {
                                    C[b] = c, f = b;
                                    break
                                }
                                if (d > e.length) {
                                    y(b + 1, n);
                                    break
                                }
                            } else C[b] === e.charAt(d) && d++, k > b && (f = b);
                            return a ? z() : k > f + 1 ? g.autoclear || C.join("") === D ? (B.val() && B.val(""), y(0, n)) : z() : (z(), B.val(B.val().substring(0, f + 1))), k ? b : l
                        }

                        var B = a(this), C = a.map(c.split(""), function (a, b) {
                            return "?" != a ? i[a] ? p(b) : a : void 0
                        }), D = C.join(""), E = B.val();
                        B.data(a.mask.dataName, function () {
                            return a.map(C, function (a, b) {
                                return j[b] && a != p(b) ? a : null
                            }).join("")
                        }), B.one("unmask", function () {
                            B.off(".mask").removeData(a.mask.dataName)
                        }).on("focus.mask", function () {
                            if (!B.prop("readonly")) {
                                clearTimeout(b);
                                var a;
                                E = B.val(), a = A(), b = setTimeout(function () {
                                    B.get(0) === document.activeElement && (z(), a == c.replace("?", "").length ? B.caret(0, a) : B.caret(a))
                                }, 10)
                            }
                        }).on("blur.mask", v).on("keydown.mask", w).on("keypress.mask", x).on("input.mask paste.mask", function () {
                            B.prop("readonly") || setTimeout(function () {
                                var a = A(!0);
                                B.caret(a), h()
                            }, 0)
                        }), e && f && B.off("input.mask").on("input.mask", u), A()
                    })
                }
            })
        });
        var form = function () {
            var $selectList = $('.selectList');
            var $input = $('.form-input, .form-textarea');
            var $form = $('.form');
            var $select = $('.form-select');
            return {
                init: function () {
                    $selectList.each(function () {
                        var $this = $(this), $radio = $this.find('input[type="radio"]');

                        function changeTitle($block, $element) {
                            $block.find('.selectList-title').text($element.closest('.selectList-item').find('.selectList-text').text())
                        }

                        changeTitle($this, $radio.filter('[checked="checked"]'));
                        $radio.on('change', function () {
                            changeTitle($this, $(this));
                        });
                    });
                    $(document).on('click', function (e) {
                        var $this = $(e.target);
                        if (!$this.hasClass('selectList-header')) {
                            $this = $(e.target).closest('.selectList-header');
                        }
                        if ($this.length) {
                            e.preventDefault();
                            $this.closest('.selectList').toggleClass('selectList_OPEN');
                        } else {
                            $('.selectList').removeClass('selectList_OPEN');
                        }
                    });
                    $input.on('blur', function () {
                        var $this = $(this), validate = $this.data('validate'), message = '', error = false;
                        if (validate) {
                            validate = validate.split(' ');
                            validate.forEach(function (v) {
                                switch (v) {
                                    case'require':
                                        if (!$this.val() && !$this.prop('disabled')) {
                                            message += 'Это поле обязательно для заполнения. ';
                                            error = true;
                                        }
                                        break;
                                    case'mail':
                                        if ($this.val() !== '' && !$this.val().match(/\w+@\w+\.\w+/) && !$this.prop('disabled')) {
                                            message += 'Нужно ввести адрес почты в формате xxx@xxx.xx';
                                            error = true;
                                        }
                                        break;
                                    case'key':
                                        if ($this.val() !== '' && !$this.val().replace(' ', '').match(/\d{4}/) && !$this.prop('disabled')) {
                                            message += 'Код должен состоять из 4 цифр';
                                            error = true;
                                        }
                                }
                            });
                            if (error) {
                                if ($this.hasClass('form-input')) {
                                    $this.addClass('form-input_error');
                                }
                                if ($this.hasClass('form-textarea')) {
                                    $this.addClass('form-textarea_error');
                                }
                                if (!$this.next('.form-error').length) {
                                    $this.after('<div class="form-error">' + message + '</div>');
                                } else {
                                    $this.next('.form-error').text(message);
                                }
                                $this.data('errorinput', true);
                            } else {
                                $this.next('.form-error').remove();
                                $this.removeClass('form-input_error');
                                $this.removeClass('form-textarea_error');
                                $this.data('errorinput', false);
                            }
                            message = '';
                        }
                    });
                    $form.on('submit', function (e) {
                        var $this = $(this), $validate = $this.find('[data-validate]');
                        $validate.each(function () {
                            var $this = $(this);
                            $this.trigger('blur');
                            if ($this.data('errorinput')) {
                                e.preventDefault();
                            }
                        });
                    });
                    $select.wrap('<div class="form-selectWrap"></div>');
                    $('[data-mask]').each(function () {
                        var $this = $(this);
                        $this.mask($this.data('mask'), {placeholder: 'x'});
                    });
                }
            };
        };
        form().init();
        !function (t, e, i) {
            !function () {
                var s, a, n, h = "2.2.3", o = "datepicker", r = ".datepicker-here", c = !1,
                    d = '<div class="datepicker"><i class="datepicker--pointer"></i><nav class="datepicker--nav"></nav><div class="datepicker--content"></div></div>',
                    l = {
                        classes: "",
                        inline: !1,
                        language: "ru",
                        startDate: new Date,
                        firstDay: "",
                        weekends: [6, 0],
                        dateFormat: "",
                        altField: "",
                        altFieldDateFormat: "@",
                        toggleSelected: !0,
                        keyboardNav: !0,
                        position: "bottom left",
                        offset: 12,
                        view: "days",
                        minView: "days",
                        showOtherMonths: !0,
                        selectOtherMonths: !0,
                        moveToOtherMonthsOnSelect: !0,
                        showOtherYears: !0,
                        selectOtherYears: !0,
                        moveToOtherYearsOnSelect: !0,
                        minDate: "",
                        maxDate: "",
                        disableNavWhenOutOfRange: !0,
                        multipleDates: !1,
                        multipleDatesSeparator: ",",
                        range: !1,
                        todayButton: !1,
                        clearButton: !1,
                        showEvent: "focus",
                        autoClose: !1,
                        monthsField: "monthsShort",
                        prevHtml: '<svg><path d="M 17,12 l -5,5 l 5,5"></path></svg>',
                        nextHtml: '<svg><path d="M 14,12 l 5,5 l -5,5"></path></svg>',
                        navTitles: {days: "MM, <i>yyyy</i>", months: "yyyy", years: "yyyy1 - yyyy2"},
                        timepicker: !1,
                        onlyTimepicker: !1,
                        dateTimeSeparator: " ",
                        timeFormat: "",
                        minHours: 0,
                        maxHours: 24,
                        minMinutes: 0,
                        maxMinutes: 59,
                        hoursStep: 1,
                        minutesStep: 1,
                        onSelect: "",
                        onShow: "",
                        onHide: "",
                        onChangeMonth: "",
                        onChangeYear: "",
                        onChangeDecade: "",
                        onChangeView: "",
                        onRenderCell: ""
                    }, u = {
                        ctrlRight: [17, 39],
                        ctrlUp: [17, 38],
                        ctrlLeft: [17, 37],
                        ctrlDown: [17, 40],
                        shiftRight: [16, 39],
                        shiftUp: [16, 38],
                        shiftLeft: [16, 37],
                        shiftDown: [16, 40],
                        altUp: [18, 38],
                        altRight: [18, 39],
                        altLeft: [18, 37],
                        altDown: [18, 40],
                        ctrlShiftUp: [16, 17, 38]
                    }, m = function (t, a) {
                        this.el = t, this.$el = e(t), this.opts = e.extend(!0, {}, l, a, this.$el.data()), s == i && (s = e("body")), this.opts.startDate || (this.opts.startDate = new Date), "INPUT" == this.el.nodeName && (this.elIsInput = !0), this.opts.altField && (this.$altField = "string" == typeof this.opts.altField ? e(this.opts.altField) : this.opts.altField), this.inited = !1, this.visible = !1, this.silent = !1, this.currentDate = this.opts.startDate, this.currentView = this.opts.view, this._createShortCuts(), this.selectedDates = [], this.views = {}, this.keys = [], this.minRange = "", this.maxRange = "", this._prevOnSelectValue = "", this.init()
                    };
                n = m, n.prototype = {
                    VERSION: h, viewIndexes: ["days", "months", "years"], init: function () {
                        c || this.opts.inline || !this.elIsInput || this._buildDatepickersContainer(), this._buildBaseHtml(), this._defineLocale(this.opts.language), this._syncWithMinMaxDates(), this.elIsInput && (this.opts.inline || (this._setPositionClasses(this.opts.position), this._bindEvents()), this.opts.keyboardNav && !this.opts.onlyTimepicker && this._bindKeyboardEvents(), this.$datepicker.on("mousedown", this._onMouseDownDatepicker.bind(this)), this.$datepicker.on("mouseup", this._onMouseUpDatepicker.bind(this))), this.opts.classes && this.$datepicker.addClass(this.opts.classes), this.opts.timepicker && (this.timepicker = new e.fn.datepicker.Timepicker(this, this.opts), this._bindTimepickerEvents()), this.opts.onlyTimepicker && this.$datepicker.addClass("-only-timepicker-"), this.views[this.currentView] = new e.fn.datepicker.Body(this, this.currentView, this.opts), this.views[this.currentView].show(), this.nav = new e.fn.datepicker.Navigation(this, this.opts), this.view = this.currentView, this.$el.on("clickCell.adp", this._onClickCell.bind(this)), this.$datepicker.on("mouseenter", ".datepicker--cell", this._onMouseEnterCell.bind(this)), this.$datepicker.on("mouseleave", ".datepicker--cell", this._onMouseLeaveCell.bind(this)), this.inited = !0
                    }, _createShortCuts: function () {
                        this.minDate = this.opts.minDate ? this.opts.minDate : new Date(-86399999136e5), this.maxDate = this.opts.maxDate ? this.opts.maxDate : new Date(86399999136e5)
                    }, _bindEvents: function () {
                        this.$el.on(this.opts.showEvent + ".adp", this._onShowEvent.bind(this)), this.$el.on("mouseup.adp", this._onMouseUpEl.bind(this)), this.$el.on("blur.adp", this._onBlur.bind(this)), this.$el.on("keyup.adp", this._onKeyUpGeneral.bind(this)), e(t).on("resize.adp", this._onResize.bind(this)), e("body").on("mouseup.adp", this._onMouseUpBody.bind(this))
                    }, _bindKeyboardEvents: function () {
                        this.$el.on("keydown.adp", this._onKeyDown.bind(this)), this.$el.on("keyup.adp", this._onKeyUp.bind(this)), this.$el.on("hotKey.adp", this._onHotKey.bind(this))
                    }, _bindTimepickerEvents: function () {
                        this.$el.on("timeChange.adp", this._onTimeChange.bind(this))
                    }, isWeekend: function (t) {
                        return -1 !== this.opts.weekends.indexOf(t)
                    }, _defineLocale: function (t) {
                        "string" == typeof t ? (this.loc = e.fn.datepicker.language[t], this.loc || (console.warn("Can't find language \"" + t + '" in Datepicker.language, will use "ru" instead'), this.loc = e.extend(!0, {}, e.fn.datepicker.language.ru)), this.loc = e.extend(!0, {}, e.fn.datepicker.language.ru, e.fn.datepicker.language[t])) : this.loc = e.extend(!0, {}, e.fn.datepicker.language.ru, t), this.opts.dateFormat && (this.loc.dateFormat = this.opts.dateFormat), this.opts.timeFormat && (this.loc.timeFormat = this.opts.timeFormat), "" !== this.opts.firstDay && (this.loc.firstDay = this.opts.firstDay), this.opts.timepicker && (this.loc.dateFormat = [this.loc.dateFormat, this.loc.timeFormat].join(this.opts.dateTimeSeparator)), this.opts.onlyTimepicker && (this.loc.dateFormat = this.loc.timeFormat);
                        var i = this._getWordBoundaryRegExp;
                        (this.loc.timeFormat.match(i("aa")) || this.loc.timeFormat.match(i("AA"))) && (this.ampm = !0)
                    }, _buildDatepickersContainer: function () {
                        c = !0, s.append('<div class="datepickers-container" id="datepickers-container"></div>'), a = e("#datepickers-container")
                    }, _buildBaseHtml: function () {
                        var t, i = e('<div class="datepicker-inline">');
                        t = "INPUT" == this.el.nodeName ? this.opts.inline ? i.insertAfter(this.$el) : a : i.appendTo(this.$el), this.$datepicker = e(d).appendTo(t), this.$content = e(".datepicker--content", this.$datepicker), this.$nav = e(".datepicker--nav", this.$datepicker)
                    }, _triggerOnChange: function () {
                        if (!this.selectedDates.length) {
                            if ("" === this._prevOnSelectValue) return;
                            return this._prevOnSelectValue = "", this.opts.onSelect("", "", this)
                        }
                        var t, e = this.selectedDates, i = n.getParsedDate(e[0]), s = this,
                            a = new Date(i.year, i.month, i.date, i.hours, i.minutes);
                        t = e.map(function (t) {
                            return s.formatDate(s.loc.dateFormat, t)
                        }).join(this.opts.multipleDatesSeparator), (this.opts.multipleDates || this.opts.range) && (a = e.map(function (t) {
                            var e = n.getParsedDate(t);
                            return new Date(e.year, e.month, e.date, e.hours, e.minutes)
                        })), this._prevOnSelectValue = t, this.opts.onSelect(t, a, this)
                    }, next: function () {
                        var t = this.parsedDate, e = this.opts;
                        switch (this.view) {
                            case"days":
                                this.date = new Date(t.year, t.month + 1, 1), e.onChangeMonth && e.onChangeMonth(this.parsedDate.month, this.parsedDate.year);
                                break;
                            case"months":
                                this.date = new Date(t.year + 1, t.month, 1), e.onChangeYear && e.onChangeYear(this.parsedDate.year);
                                break;
                            case"years":
                                this.date = new Date(t.year + 10, 0, 1), e.onChangeDecade && e.onChangeDecade(this.curDecade)
                        }
                    }, prev: function () {
                        var t = this.parsedDate, e = this.opts;
                        switch (this.view) {
                            case"days":
                                this.date = new Date(t.year, t.month - 1, 1), e.onChangeMonth && e.onChangeMonth(this.parsedDate.month, this.parsedDate.year);
                                break;
                            case"months":
                                this.date = new Date(t.year - 1, t.month, 1), e.onChangeYear && e.onChangeYear(this.parsedDate.year);
                                break;
                            case"years":
                                this.date = new Date(t.year - 10, 0, 1), e.onChangeDecade && e.onChangeDecade(this.curDecade)
                        }
                    }, formatDate: function (t, e) {
                        e = e || this.date;
                        var i, s = t, a = this._getWordBoundaryRegExp, h = this.loc, o = n.getLeadingZeroNum,
                            r = n.getDecade(e), c = n.getParsedDate(e), d = c.fullHours, l = c.hours,
                            u = t.match(a("aa")) || t.match(a("AA")), m = "am", p = this._replacer;
                        switch (this.opts.timepicker && this.timepicker && u && (i = this.timepicker._getValidHoursFromDate(e, u), d = o(i.hours), l = i.hours, m = i.dayPeriod), !0) {
                            case/@/.test(s):
                                s = s.replace(/@/, e.getTime());
                            case/aa/.test(s):
                                s = p(s, a("aa"), m);
                            case/AA/.test(s):
                                s = p(s, a("AA"), m.toUpperCase());
                            case/dd/.test(s):
                                s = p(s, a("dd"), c.fullDate);
                            case/d/.test(s):
                                s = p(s, a("d"), c.date);
                            case/DD/.test(s):
                                s = p(s, a("DD"), h.days[c.day]);
                            case/D/.test(s):
                                s = p(s, a("D"), h.daysShort[c.day]);
                            case/mm/.test(s):
                                s = p(s, a("mm"), c.fullMonth);
                            case/m/.test(s):
                                s = p(s, a("m"), c.month + 1);
                            case/MM/.test(s):
                                s = p(s, a("MM"), this.loc.months[c.month]);
                            case/M/.test(s):
                                s = p(s, a("M"), h.monthsShort[c.month]);
                            case/ii/.test(s):
                                s = p(s, a("ii"), c.fullMinutes);
                            case/i/.test(s):
                                s = p(s, a("i"), c.minutes);
                            case/hh/.test(s):
                                s = p(s, a("hh"), d);
                            case/h/.test(s):
                                s = p(s, a("h"), l);
                            case/yyyy/.test(s):
                                s = p(s, a("yyyy"), c.year);
                            case/yyyy1/.test(s):
                                s = p(s, a("yyyy1"), r[0]);
                            case/yyyy2/.test(s):
                                s = p(s, a("yyyy2"), r[1]);
                            case/yy/.test(s):
                                s = p(s, a("yy"), c.year.toString().slice(-2))
                        }
                        return s
                    }, _replacer: function (t, e, i) {
                        return t.replace(e, function (t, e, s, a) {
                            return e + i + a
                        })
                    }, _getWordBoundaryRegExp: function (t) {
                        var e = "\\s|\\.|-|/|\\\\|,|\\$|\\!|\\?|:|;";
                        return new RegExp("(^|>|" + e + ")(" + t + ")($|<|" + e + ")", "g")
                    }, selectDate: function (t) {
                        var e = this, i = e.opts, s = e.parsedDate, a = e.selectedDates, h = a.length, o = "";
                        if (Array.isArray(t)) return void t.forEach(function (t) {
                            e.selectDate(t)
                        });
                        if (t instanceof Date) {
                            if (this.lastSelectedDate = t, this.timepicker && this.timepicker._setTime(t), e._trigger("selectDate", t), this.timepicker && (t.setHours(this.timepicker.hours), t.setMinutes(this.timepicker.minutes)), "days" == e.view && t.getMonth() != s.month && i.moveToOtherMonthsOnSelect && (o = new Date(t.getFullYear(), t.getMonth(), 1)), "years" == e.view && t.getFullYear() != s.year && i.moveToOtherYearsOnSelect && (o = new Date(t.getFullYear(), 0, 1)), o && (e.silent = !0, e.date = o, e.silent = !1, e.nav._render()), i.multipleDates && !i.range) {
                                if (h === i.multipleDates) return;
                                e._isSelected(t) || e.selectedDates.push(t)
                            } else i.range ? 2 == h ? (e.selectedDates = [t], e.minRange = t, e.maxRange = "") : 1 == h ? (e.selectedDates.push(t), e.maxRange ? e.minRange = t : e.maxRange = t, n.bigger(e.maxRange, e.minRange) && (e.maxRange = e.minRange, e.minRange = t), e.selectedDates = [e.minRange, e.maxRange]) : (e.selectedDates = [t], e.minRange = t) : e.selectedDates = [t];
                            e._setInputValue(), i.onSelect && e._triggerOnChange(), i.autoClose && !this.timepickerIsActive && (i.multipleDates || i.range ? i.range && 2 == e.selectedDates.length && e.hide() : e.hide()), e.views[this.currentView]._render()
                        }
                    }, removeDate: function (t) {
                        var e = this.selectedDates, i = this;
                        if (t instanceof Date) return e.some(function (s, a) {
                            return n.isSame(s, t) ? (e.splice(a, 1), i.selectedDates.length ? i.lastSelectedDate = i.selectedDates[i.selectedDates.length - 1] : (i.minRange = "", i.maxRange = "", i.lastSelectedDate = ""), i.views[i.currentView]._render(), i._setInputValue(), i.opts.onSelect && i._triggerOnChange(), !0) : void 0
                        })
                    }, today: function () {
                        this.silent = !0, this.view = this.opts.minView, this.silent = !1, this.date = new Date, this.opts.todayButton instanceof Date && this.selectDate(this.opts.todayButton)
                    }, clear: function () {
                        this.selectedDates = [], this.minRange = "", this.maxRange = "", this.views[this.currentView]._render(), this._setInputValue(), this.opts.onSelect && this._triggerOnChange()
                    }, update: function (t, i) {
                        var s = arguments.length, a = this.lastSelectedDate;
                        return 2 == s ? this.opts[t] = i : 1 == s && "object" == typeof t && (this.opts = e.extend(!0, this.opts, t)), this._createShortCuts(), this._syncWithMinMaxDates(), this._defineLocale(this.opts.language), this.nav._addButtonsIfNeed(), this.opts.onlyTimepicker || this.nav._render(), this.views[this.currentView]._render(), this.elIsInput && !this.opts.inline && (this._setPositionClasses(this.opts.position), this.visible && this.setPosition(this.opts.position)), this.opts.classes && this.$datepicker.addClass(this.opts.classes), this.opts.onlyTimepicker && this.$datepicker.addClass("-only-timepicker-"), this.opts.timepicker && (a && this.timepicker._handleDate(a), this.timepicker._updateRanges(), this.timepicker._updateCurrentTime(), a && (a.setHours(this.timepicker.hours), a.setMinutes(this.timepicker.minutes))), this._setInputValue(), this
                    }, _syncWithMinMaxDates: function () {
                        var t = this.date.getTime();
                        this.silent = !0, this.minTime > t && (this.date = this.minDate), this.maxTime < t && (this.date = this.maxDate), this.silent = !1
                    }, _isSelected: function (t, e) {
                        var i = !1;
                        return this.selectedDates.some(function (s) {
                            return n.isSame(s, t, e) ? (i = s, !0) : void 0
                        }), i
                    }, _setInputValue: function () {
                        var t, e = this, i = e.opts, s = e.loc.dateFormat, a = i.altFieldDateFormat,
                            n = e.selectedDates.map(function (t) {
                                return e.formatDate(s, t)
                            });
                        i.altField && e.$altField.length && (t = this.selectedDates.map(function (t) {
                            return e.formatDate(a, t)
                        }), t = t.join(this.opts.multipleDatesSeparator), this.$altField.val(t)), n = n.join(this.opts.multipleDatesSeparator), this.$el.val(n)
                    }, _isInRange: function (t, e) {
                        var i = t.getTime(), s = n.getParsedDate(t), a = n.getParsedDate(this.minDate),
                            h = n.getParsedDate(this.maxDate), o = new Date(s.year, s.month, a.date).getTime(),
                            r = new Date(s.year, s.month, h.date).getTime(), c = {
                                day: i >= this.minTime && i <= this.maxTime,
                                month: o >= this.minTime && r <= this.maxTime,
                                year: s.year >= a.year && s.year <= h.year
                            };
                        return e ? c[e] : c.day
                    }, _getDimensions: function (t) {
                        var e = t.offset();
                        return {width: t.outerWidth(), height: t.outerHeight(), left: e.left, top: e.top}
                    }, _getDateFromCell: function (t) {
                        var e = this.parsedDate, s = t.data("year") || e.year,
                            a = t.data("month") == i ? e.month : t.data("month"), n = t.data("date") || 1;
                        return new Date(s, a, n)
                    }, _setPositionClasses: function (t) {
                        t = t.split(" ");
                        var e = t[0], i = t[1], s = "datepicker -" + e + "-" + i + "- -from-" + e + "-";
                        this.visible && (s += " active"), this.$datepicker.removeAttr("class").addClass(s)
                    }, setPosition: function (t) {
                        t = t || this.opts.position;
                        var e, i, s = this._getDimensions(this.$el), a = this._getDimensions(this.$datepicker),
                            n = t.split(" "), h = this.opts.offset, o = n[0], r = n[1];
                        switch (o) {
                            case"top":
                                e = s.top - a.height - h;
                                break;
                            case"right":
                                i = s.left + s.width + h;
                                break;
                            case"bottom":
                                e = s.top + s.height + h;
                                break;
                            case"left":
                                i = s.left - a.width - h
                        }
                        switch (r) {
                            case"top":
                                e = s.top;
                                break;
                            case"right":
                                i = s.left + s.width - a.width;
                                break;
                            case"bottom":
                                e = s.top + s.height - a.height;
                                break;
                            case"left":
                                i = s.left;
                                break;
                            case"center":
                                /left|right/.test(o) ? e = s.top + s.height / 2 - a.height / 2 : i = s.left + s.width / 2 - a.width / 2
                        }
                        this.$datepicker.css({left: i, top: e})
                    }, show: function () {
                        var t = this.opts.onShow;
                        this.setPosition(this.opts.position), this.$datepicker.addClass("active"), this.visible = !0, t && this._bindVisionEvents(t)
                    }, hide: function () {
                        var t = this.opts.onHide;
                        this.$datepicker.removeClass("active").css({left: "-100000px"}), this.focused = "", this.keys = [], this.inFocus = !1, this.visible = !1, this.$el.blur(), t && this._bindVisionEvents(t)
                    }, down: function (t) {
                        this._changeView(t, "down")
                    }, up: function (t) {
                        this._changeView(t, "up")
                    }, _bindVisionEvents: function (t) {
                        this.$datepicker.off("transitionend.dp"), t(this, !1), this.$datepicker.one("transitionend.dp", t.bind(this, this, !0))
                    }, _changeView: function (t, e) {
                        t = t || this.focused || this.date;
                        var i = "up" == e ? this.viewIndex + 1 : this.viewIndex - 1;
                        i > 2 && (i = 2), 0 > i && (i = 0), this.silent = !0, this.date = new Date(t.getFullYear(), t.getMonth(), 1), this.silent = !1, this.view = this.viewIndexes[i]
                    }, _handleHotKey: function (t) {
                        var e, i, s, a = n.getParsedDate(this._getFocusedDate()), h = this.opts, o = !1, r = !1, c = !1,
                            d = a.year, l = a.month, u = a.date;
                        switch (t) {
                            case"ctrlRight":
                            case"ctrlUp":
                                l += 1, o = !0;
                                break;
                            case"ctrlLeft":
                            case"ctrlDown":
                                l -= 1, o = !0;
                                break;
                            case"shiftRight":
                            case"shiftUp":
                                r = !0, d += 1;
                                break;
                            case"shiftLeft":
                            case"shiftDown":
                                r = !0, d -= 1;
                                break;
                            case"altRight":
                            case"altUp":
                                c = !0, d += 10;
                                break;
                            case"altLeft":
                            case"altDown":
                                c = !0, d -= 10;
                                break;
                            case"ctrlShiftUp":
                                this.up()
                        }
                        s = n.getDaysCount(new Date(d, l)), i = new Date(d, l, u), u > s && (u = s), i.getTime() < this.minTime ? i = this.minDate : i.getTime() > this.maxTime && (i = this.maxDate), this.focused = i, e = n.getParsedDate(i), o && h.onChangeMonth && h.onChangeMonth(e.month, e.year), r && h.onChangeYear && h.onChangeYear(e.year), c && h.onChangeDecade && h.onChangeDecade(this.curDecade)
                    }, _registerKey: function (t) {
                        var e = this.keys.some(function (e) {
                            return e == t
                        });
                        e || this.keys.push(t)
                    }, _unRegisterKey: function (t) {
                        var e = this.keys.indexOf(t);
                        this.keys.splice(e, 1)
                    }, _isHotKeyPressed: function () {
                        var t, e = !1, i = this, s = this.keys.sort();
                        for (var a in u) t = u[a], s.length == t.length && t.every(function (t, e) {
                            return t == s[e]
                        }) && (i._trigger("hotKey", a), e = !0);
                        return e
                    }, _trigger: function (t, e) {
                        this.$el.trigger(t, e)
                    }, _focusNextCell: function (t, e) {
                        e = e || this.cellType;
                        var i = n.getParsedDate(this._getFocusedDate()), s = i.year, a = i.month, h = i.date;
                        if (!this._isHotKeyPressed()) {
                            switch (t) {
                                case 37:
                                    "day" == e ? h -= 1 : "", "month" == e ? a -= 1 : "", "year" == e ? s -= 1 : "";
                                    break;
                                case 38:
                                    "day" == e ? h -= 7 : "", "month" == e ? a -= 3 : "", "year" == e ? s -= 4 : "";
                                    break;
                                case 39:
                                    "day" == e ? h += 1 : "", "month" == e ? a += 1 : "", "year" == e ? s += 1 : "";
                                    break;
                                case 40:
                                    "day" == e ? h += 7 : "", "month" == e ? a += 3 : "", "year" == e ? s += 4 : ""
                            }
                            var o = new Date(s, a, h);
                            o.getTime() < this.minTime ? o = this.minDate : o.getTime() > this.maxTime && (o = this.maxDate), this.focused = o
                        }
                    }, _getFocusedDate: function () {
                        var t = this.focused || this.selectedDates[this.selectedDates.length - 1], e = this.parsedDate;
                        if (!t) switch (this.view) {
                            case"days":
                                t = new Date(e.year, e.month, (new Date).getDate());
                                break;
                            case"months":
                                t = new Date(e.year, e.month, 1);
                                break;
                            case"years":
                                t = new Date(e.year, 0, 1)
                        }
                        return t
                    }, _getCell: function (t, i) {
                        i = i || this.cellType;
                        var s, a = n.getParsedDate(t), h = '.datepicker--cell[data-year="' + a.year + '"]';
                        switch (i) {
                            case"month":
                                h = '[data-month="' + a.month + '"]';
                                break;
                            case"day":
                                h += '[data-month="' + a.month + '"][data-date="' + a.date + '"]'
                        }
                        return s = this.views[this.currentView].$el.find(h), s.length ? s : e("")
                    }, destroy: function () {
                        var t = this;
                        t.$el.off(".adp").data("datepicker", ""), t.selectedDates = [], t.focused = "", t.views = {}, t.keys = [], t.minRange = "", t.maxRange = "", t.opts.inline || !t.elIsInput ? t.$datepicker.closest(".datepicker-inline").remove() : t.$datepicker.remove()
                    }, _handleAlreadySelectedDates: function (t, e) {
                        this.opts.range ? this.opts.toggleSelected ? this.removeDate(e) : 2 != this.selectedDates.length && this._trigger("clickCell", e) : this.opts.toggleSelected && this.removeDate(e), this.opts.toggleSelected || (this.lastSelectedDate = t, this.opts.timepicker && (this.timepicker._setTime(t), this.timepicker.update()))
                    }, _onShowEvent: function (t) {
                        this.visible || this.show()
                    }, _onBlur: function () {
                        !this.inFocus && this.visible && this.hide()
                    }, _onMouseDownDatepicker: function (t) {
                        this.inFocus = !0
                    }, _onMouseUpDatepicker: function (t) {
                        this.inFocus = !1, t.originalEvent.inFocus = !0, t.originalEvent.timepickerFocus || this.$el.focus()
                    }, _onKeyUpGeneral: function (t) {
                        var e = this.$el.val();
                        e || this.clear()
                    }, _onResize: function () {
                        this.visible && this.setPosition()
                    }, _onMouseUpBody: function (t) {
                        t.originalEvent.inFocus || this.visible && !this.inFocus && this.hide()
                    }, _onMouseUpEl: function (t) {
                        t.originalEvent.inFocus = !0, setTimeout(this._onKeyUpGeneral.bind(this), 4)
                    }, _onKeyDown: function (t) {
                        var e = t.which;
                        if (this._registerKey(e), e >= 37 && 40 >= e && (t.preventDefault(), this._focusNextCell(e)), 13 == e && this.focused) {
                            if (this._getCell(this.focused).hasClass("-disabled-")) return;
                            if (this.view != this.opts.minView) this.down(); else {
                                var i = this._isSelected(this.focused, this.cellType);
                                if (!i) return this.timepicker && (this.focused.setHours(this.timepicker.hours), this.focused.setMinutes(this.timepicker.minutes)), void this.selectDate(this.focused);
                                this._handleAlreadySelectedDates(i, this.focused)
                            }
                        }
                        27 == e && this.hide()
                    }, _onKeyUp: function (t) {
                        var e = t.which;
                        this._unRegisterKey(e)
                    }, _onHotKey: function (t, e) {
                        this._handleHotKey(e)
                    }, _onMouseEnterCell: function (t) {
                        var i = e(t.target).closest(".datepicker--cell"), s = this._getDateFromCell(i);
                        this.silent = !0, this.focused && (this.focused = ""), i.addClass("-focus-"), this.focused = s, this.silent = !1, this.opts.range && 1 == this.selectedDates.length && (this.minRange = this.selectedDates[0], this.maxRange = "", n.less(this.minRange, this.focused) && (this.maxRange = this.minRange, this.minRange = ""), this.views[this.currentView]._update())
                    }, _onMouseLeaveCell: function (t) {
                        var i = e(t.target).closest(".datepicker--cell");
                        i.removeClass("-focus-"), this.silent = !0, this.focused = "", this.silent = !1
                    }, _onTimeChange: function (t, e, i) {
                        var s = new Date, a = this.selectedDates, n = !1;
                        a.length && (n = !0, s = this.lastSelectedDate), s.setHours(e), s.setMinutes(i), n || this._getCell(s).hasClass("-disabled-") ? (this._setInputValue(), this.opts.onSelect && this._triggerOnChange()) : this.selectDate(s)
                    }, _onClickCell: function (t, e) {
                        this.timepicker && (e.setHours(this.timepicker.hours), e.setMinutes(this.timepicker.minutes)), this.selectDate(e)
                    }, set focused(t) {
                        if (!t && this.focused) {
                            var e = this._getCell(this.focused);
                            e.length && e.removeClass("-focus-")
                        }
                        this._focused = t, this.opts.range && 1 == this.selectedDates.length && (this.minRange = this.selectedDates[0], this.maxRange = "", n.less(this.minRange, this._focused) && (this.maxRange = this.minRange, this.minRange = "")), this.silent || (this.date = t)
                    }, get focused() {
                        return this._focused
                    }, get parsedDate() {
                        return n.getParsedDate(this.date)
                    }, set date(t) {
                        return t instanceof Date ? (this.currentDate = t, this.inited && !this.silent && (this.views[this.view]._render(), this.nav._render(), this.visible && this.elIsInput && this.setPosition()), t) : void 0
                    }, get date() {
                        return this.currentDate
                    }, set view(t) {
                        return this.viewIndex = this.viewIndexes.indexOf(t), this.viewIndex < 0 ? void 0 : (this.prevView = this.currentView, this.currentView = t, this.inited && (this.views[t] ? this.views[t]._render() : this.views[t] = new e.fn.datepicker.Body(this, t, this.opts), this.views[this.prevView].hide(), this.views[t].show(), this.nav._render(), this.opts.onChangeView && this.opts.onChangeView(t), this.elIsInput && this.visible && this.setPosition()), t)
                    }, get view() {
                        return this.currentView
                    }, get cellType() {
                        return this.view.substring(0, this.view.length - 1)
                    }, get minTime() {
                        var t = n.getParsedDate(this.minDate);
                        return new Date(t.year, t.month, t.date).getTime()
                    }, get maxTime() {
                        var t = n.getParsedDate(this.maxDate);
                        return new Date(t.year, t.month, t.date).getTime()
                    }, get curDecade() {
                        return n.getDecade(this.date)
                    }
                }, n.getDaysCount = function (t) {
                    return new Date(t.getFullYear(), t.getMonth() + 1, 0).getDate()
                }, n.getParsedDate = function (t) {
                    return {
                        year: t.getFullYear(),
                        month: t.getMonth(),
                        fullMonth: t.getMonth() + 1 < 10 ? "0" + (t.getMonth() + 1) : t.getMonth() + 1,
                        date: t.getDate(),
                        fullDate: t.getDate() < 10 ? "0" + t.getDate() : t.getDate(),
                        day: t.getDay(),
                        hours: t.getHours(),
                        fullHours: t.getHours() < 10 ? "0" + t.getHours() : t.getHours(),
                        minutes: t.getMinutes(),
                        fullMinutes: t.getMinutes() < 10 ? "0" + t.getMinutes() : t.getMinutes()
                    }
                }, n.getDecade = function (t) {
                    var e = 10 * Math.floor(t.getFullYear() / 10);
                    return [e, e + 9]
                }, n.template = function (t, e) {
                    return t.replace(/#\{([\w]+)\}/g, function (t, i) {
                        return e[i] || 0 === e[i] ? e[i] : void 0
                    })
                }, n.isSame = function (t, e, i) {
                    if (!t || !e) return !1;
                    var s = n.getParsedDate(t), a = n.getParsedDate(e), h = i ? i : "day", o = {
                        day: s.date == a.date && s.month == a.month && s.year == a.year,
                        month: s.month == a.month && s.year == a.year,
                        year: s.year == a.year
                    };
                    return o[h]
                }, n.less = function (t, e, i) {
                    return t && e ? e.getTime() < t.getTime() : !1
                }, n.bigger = function (t, e, i) {
                    return t && e ? e.getTime() > t.getTime() : !1
                }, n.getLeadingZeroNum = function (t) {
                    return parseInt(t) < 10 ? "0" + t : t
                }, n.resetTime = function (t) {
                    return "object" == typeof t ? (t = n.getParsedDate(t), new Date(t.year, t.month, t.date)) : void 0
                }, e.fn.datepicker = function (t) {
                    return this.each(function () {
                        if (e.data(this, o)) {
                            var i = e.data(this, o);
                            i.opts = e.extend(!0, i.opts, t), i.update()
                        } else e.data(this, o, new m(this, t))
                    })
                }, e.fn.datepicker.Constructor = m, e.fn.datepicker.language = {
                    ru: {
                        days: ["Воскресенье", "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота"],
                        daysShort: ["Вос", "Пон", "Вто", "Сре", "Чет", "Пят", "Суб"],
                        daysMin: ["Вс", "Пн", "Вт", "Ср", "Чт", "Пт", "Сб"],
                        months: ["Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"],
                        monthsShort: ["Янв", "Фев", "Мар", "Апр", "Май", "Июн", "Июл", "Авг", "Сен", "Окт", "Ноя", "Дек"],
                        today: "Сегодня",
                        clear: "Очистить",
                        dateFormat: "dd.mm.yyyy",
                        timeFormat: "hh:ii",
                        firstDay: 1
                    }
                }, e(function () {
                    e(r).datepicker()
                })
            }(), function () {
                var t = {
                    days: '<div class="datepicker--days datepicker--body"><div class="datepicker--days-names"></div><div class="datepicker--cells datepicker--cells-days"></div></div>',
                    months: '<div class="datepicker--months datepicker--body"><div class="datepicker--cells datepicker--cells-months"></div></div>',
                    years: '<div class="datepicker--years datepicker--body"><div class="datepicker--cells datepicker--cells-years"></div></div>'
                }, s = e.fn.datepicker, a = s.Constructor;
                s.Body = function (t, i, s) {
                    this.d = t, this.type = i, this.opts = s, this.$el = e(""), this.opts.onlyTimepicker || this.init()
                }, s.Body.prototype = {
                    init: function () {
                        this._buildBaseHtml(), this._render(), this._bindEvents()
                    }, _bindEvents: function () {
                        this.$el.on("click", ".datepicker--cell", e.proxy(this._onClickCell, this))
                    }, _buildBaseHtml: function () {
                        this.$el = e(t[this.type]).appendTo(this.d.$content), this.$names = e(".datepicker--days-names", this.$el), this.$cells = e(".datepicker--cells", this.$el)
                    }, _getDayNamesHtml: function (t, e, s, a) {
                        return e = e != i ? e : t, s = s ? s : "", a = a != i ? a : 0, a > 7 ? s : 7 == e ? this._getDayNamesHtml(t, 0, s, ++a) : (s += '<div class="datepicker--day-name' + (this.d.isWeekend(e) ? " -weekend-" : "") + '">' + this.d.loc.daysMin[e] + "</div>", this._getDayNamesHtml(t, ++e, s, ++a))
                    }, _getCellContents: function (t, e) {
                        var i = "datepicker--cell datepicker--cell-" + e, s = new Date, n = this.d,
                            h = a.resetTime(n.minRange), o = a.resetTime(n.maxRange), r = n.opts,
                            c = a.getParsedDate(t), d = {}, l = c.date;
                        switch (e) {
                            case"day":
                                n.isWeekend(c.day) && (i += " -weekend-"), c.month != this.d.parsedDate.month && (i += " -other-month-", r.selectOtherMonths || (i += " -disabled-"), r.showOtherMonths || (l = ""));
                                break;
                            case"month":
                                l = n.loc[n.opts.monthsField][c.month];
                                break;
                            case"year":
                                var u = n.curDecade;
                                l = c.year, (c.year < u[0] || c.year > u[1]) && (i += " -other-decade-", r.selectOtherYears || (i += " -disabled-"), r.showOtherYears || (l = ""))
                        }
                        return r.onRenderCell && (d = r.onRenderCell(t, e) || {}, l = d.html ? d.html : l, i += d.classes ? " " + d.classes : ""), r.range && (a.isSame(h, t, e) && (i += " -range-from-"), a.isSame(o, t, e) && (i += " -range-to-"), 1 == n.selectedDates.length && n.focused ? ((a.bigger(h, t) && a.less(n.focused, t) || a.less(o, t) && a.bigger(n.focused, t)) && (i += " -in-range-"), a.less(o, t) && a.isSame(n.focused, t) && (i += " -range-from-"), a.bigger(h, t) && a.isSame(n.focused, t) && (i += " -range-to-")) : 2 == n.selectedDates.length && a.bigger(h, t) && a.less(o, t) && (i += " -in-range-")), a.isSame(s, t, e) && (i += " -current-"), n.focused && a.isSame(t, n.focused, e) && (i += " -focus-"), n._isSelected(t, e) && (i += " -selected-"), (!n._isInRange(t, e) || d.disabled) && (i += " -disabled-"), {
                            html: l,
                            classes: i
                        }
                    }, _getDaysHtml: function (t) {
                        var e = a.getDaysCount(t), i = new Date(t.getFullYear(), t.getMonth(), 1).getDay(),
                            s = new Date(t.getFullYear(), t.getMonth(), e).getDay(), n = i - this.d.loc.firstDay,
                            h = 6 - s + this.d.loc.firstDay;
                        n = 0 > n ? n + 7 : n, h = h > 6 ? h - 7 : h;
                        for (var o, r, c = -n + 1, d = "", l = c, u = e + h; u >= l; l++) r = t.getFullYear(), o = t.getMonth(), d += this._getDayHtml(new Date(r, o, l));
                        return d
                    }, _getDayHtml: function (t) {
                        var e = this._getCellContents(t, "day");
                        return '<div class="' + e.classes + '" data-date="' + t.getDate() + '" data-month="' + t.getMonth() + '" data-year="' + t.getFullYear() + '">' + e.html + "</div>"
                    }, _getMonthsHtml: function (t) {
                        for (var e = "", i = a.getParsedDate(t), s = 0; 12 > s;) e += this._getMonthHtml(new Date(i.year, s)), s++;
                        return e
                    }, _getMonthHtml: function (t) {
                        var e = this._getCellContents(t, "month");
                        return '<div class="' + e.classes + '" data-month="' + t.getMonth() + '">' + e.html + "</div>"
                    }, _getYearsHtml: function (t) {
                        var e = (a.getParsedDate(t), a.getDecade(t)), i = e[0] - 1, s = "", n = i;
                        for (n; n <= e[1] + 1; n++) s += this._getYearHtml(new Date(n, 0));
                        return s
                    }, _getYearHtml: function (t) {
                        var e = this._getCellContents(t, "year");
                        return '<div class="' + e.classes + '" data-year="' + t.getFullYear() + '">' + e.html + "</div>"
                    }, _renderTypes: {
                        days: function () {
                            var t = this._getDayNamesHtml(this.d.loc.firstDay),
                                e = this._getDaysHtml(this.d.currentDate);
                            this.$cells.html(e), this.$names.html(t)
                        }, months: function () {
                            var t = this._getMonthsHtml(this.d.currentDate);
                            this.$cells.html(t)
                        }, years: function () {
                            var t = this._getYearsHtml(this.d.currentDate);
                            this.$cells.html(t)
                        }
                    }, _render: function () {
                        this.opts.onlyTimepicker || this._renderTypes[this.type].bind(this)()
                    }, _update: function () {
                        var t, i, s, a = e(".datepicker--cell", this.$cells), n = this;
                        a.each(function (a, h) {
                            i = e(this), s = n.d._getDateFromCell(e(this)), t = n._getCellContents(s, n.d.cellType), i.attr("class", t.classes)
                        })
                    }, show: function () {
                        this.opts.onlyTimepicker || (this.$el.addClass("active"), this.acitve = !0)
                    }, hide: function () {
                        this.$el.removeClass("active"), this.active = !1
                    }, _handleClick: function (t) {
                        var e = t.data("date") || 1, i = t.data("month") || 0,
                            s = t.data("year") || this.d.parsedDate.year, a = this.d;
                        if (a.view != this.opts.minView) return void a.down(new Date(s, i, e));
                        var n = new Date(s, i, e), h = this.d._isSelected(n, this.d.cellType);
                        return h ? void a._handleAlreadySelectedDates.bind(a, h, n)() : void a._trigger("clickCell", n)
                    }, _onClickCell: function (t) {
                        var i = e(t.target).closest(".datepicker--cell");
                        i.hasClass("-disabled-") || this._handleClick.bind(this)(i)
                    }
                }
            }(), function () {
                var t = '<div class="datepicker--nav-action" data-action="prev">#{prevHtml}</div><div class="datepicker--nav-title">#{title}</div><div class="datepicker--nav-action" data-action="next">#{nextHtml}</div>',
                    i = '<div class="datepicker--buttons"></div>',
                    s = '<span class="datepicker--button" data-action="#{action}">#{label}</span>', a = e.fn.datepicker,
                    n = a.Constructor;
                a.Navigation = function (t, e) {
                    this.d = t, this.opts = e, this.$buttonsContainer = "", this.init()
                }, a.Navigation.prototype = {
                    init: function () {
                        this._buildBaseHtml(), this._bindEvents()
                    }, _bindEvents: function () {
                        this.d.$nav.on("click", ".datepicker--nav-action", e.proxy(this._onClickNavButton, this)), this.d.$nav.on("click", ".datepicker--nav-title", e.proxy(this._onClickNavTitle, this)), this.d.$datepicker.on("click", ".datepicker--button", e.proxy(this._onClickNavButton, this))
                    }, _buildBaseHtml: function () {
                        this.opts.onlyTimepicker || this._render(), this._addButtonsIfNeed()
                    }, _addButtonsIfNeed: function () {
                        this.opts.todayButton && this._addButton("today"), this.opts.clearButton && this._addButton("clear")
                    }, _render: function () {
                        var i = this._getTitle(this.d.currentDate), s = n.template(t, e.extend({title: i}, this.opts));
                        this.d.$nav.html(s), "years" == this.d.view && e(".datepicker--nav-title", this.d.$nav).addClass("-disabled-"), this.setNavStatus()
                    }, _getTitle: function (t) {
                        return this.d.formatDate(this.opts.navTitles[this.d.view], t)
                    }, _addButton: function (t) {
                        this.$buttonsContainer.length || this._addButtonsContainer();
                        var i = {action: t, label: this.d.loc[t]}, a = n.template(s, i);
                        e("[data-action=" + t + "]", this.$buttonsContainer).length || this.$buttonsContainer.append(a)
                    }, _addButtonsContainer: function () {
                        this.d.$datepicker.append(i), this.$buttonsContainer = e(".datepicker--buttons", this.d.$datepicker)
                    }, setNavStatus: function () {
                        if ((this.opts.minDate || this.opts.maxDate) && this.opts.disableNavWhenOutOfRange) {
                            var t = this.d.parsedDate, e = t.month, i = t.year, s = t.date;
                            switch (this.d.view) {
                                case"days":
                                    this.d._isInRange(new Date(i, e - 1, 1), "month") || this._disableNav("prev"), this.d._isInRange(new Date(i, e + 1, 1), "month") || this._disableNav("next");
                                    break;
                                case"months":
                                    this.d._isInRange(new Date(i - 1, e, s), "year") || this._disableNav("prev"), this.d._isInRange(new Date(i + 1, e, s), "year") || this._disableNav("next");
                                    break;
                                case"years":
                                    var a = n.getDecade(this.d.date);
                                    this.d._isInRange(new Date(a[0] - 1, 0, 1), "year") || this._disableNav("prev"), this.d._isInRange(new Date(a[1] + 1, 0, 1), "year") || this._disableNav("next")
                            }
                        }
                    }, _disableNav: function (t) {
                        e('[data-action="' + t + '"]', this.d.$nav).addClass("-disabled-")
                    }, _activateNav: function (t) {
                        e('[data-action="' + t + '"]', this.d.$nav).removeClass("-disabled-")
                    }, _onClickNavButton: function (t) {
                        var i = e(t.target).closest("[data-action]"), s = i.data("action");
                        this.d[s]()
                    }, _onClickNavTitle: function (t) {
                        return e(t.target).hasClass("-disabled-") ? void 0 : "days" == this.d.view ? this.d.view = "months" : void (this.d.view = "years")
                    }
                }
            }(), function () {
                var t = '<div class="datepicker--time"><div class="datepicker--time-current">   <span class="datepicker--time-current-hours">#{hourVisible}</span>   <span class="datepicker--time-current-colon">:</span>   <span class="datepicker--time-current-minutes">#{minValue}</span></div><div class="datepicker--time-sliders">   <div class="datepicker--time-row">      <input type="range" name="hours" value="#{hourValue}" min="#{hourMin}" max="#{hourMax}" step="#{hourStep}"/>   </div>   <div class="datepicker--time-row">      <input type="range" name="minutes" value="#{minValue}" min="#{minMin}" max="#{minMax}" step="#{minStep}"/>   </div></div></div>',
                    i = e.fn.datepicker, s = i.Constructor;
                i.Timepicker = function (t, e) {
                    this.d = t, this.opts = e, this.init()
                }, i.Timepicker.prototype = {
                    init: function () {
                        var t = "input";
                        this._setTime(this.d.date), this._buildHTML(), navigator.userAgent.match(/trident/gi) && (t = "change"), this.d.$el.on("selectDate", this._onSelectDate.bind(this)), this.$ranges.on(t, this._onChangeRange.bind(this)), this.$ranges.on("mouseup", this._onMouseUpRange.bind(this)), this.$ranges.on("mousemove focus ", this._onMouseEnterRange.bind(this)), this.$ranges.on("mouseout blur", this._onMouseOutRange.bind(this))
                    }, _setTime: function (t) {
                        var e = s.getParsedDate(t);
                        this._handleDate(t), this.hours = e.hours < this.minHours ? this.minHours : e.hours, this.minutes = e.minutes < this.minMinutes ? this.minMinutes : e.minutes
                    }, _setMinTimeFromDate: function (t) {
                        this.minHours = t.getHours(), this.minMinutes = t.getMinutes(), this.d.lastSelectedDate && this.d.lastSelectedDate.getHours() > t.getHours() && (this.minMinutes = this.opts.minMinutes)
                    }, _setMaxTimeFromDate: function (t) {
                        this.maxHours = t.getHours(), this.maxMinutes = t.getMinutes(), this.d.lastSelectedDate && this.d.lastSelectedDate.getHours() < t.getHours() && (this.maxMinutes = this.opts.maxMinutes)
                    }, _setDefaultMinMaxTime: function () {
                        var t = 23, e = 59, i = this.opts;
                        this.minHours = i.minHours < 0 || i.minHours > t ? 0 : i.minHours, this.minMinutes = i.minMinutes < 0 || i.minMinutes > e ? 0 : i.minMinutes, this.maxHours = i.maxHours < 0 || i.maxHours > t ? t : i.maxHours, this.maxMinutes = i.maxMinutes < 0 || i.maxMinutes > e ? e : i.maxMinutes
                    }, _validateHoursMinutes: function (t) {
                        this.hours < this.minHours ? this.hours = this.minHours : this.hours > this.maxHours && (this.hours = this.maxHours), this.minutes < this.minMinutes ? this.minutes = this.minMinutes : this.minutes > this.maxMinutes && (this.minutes = this.maxMinutes)
                    }, _buildHTML: function () {
                        var i = s.getLeadingZeroNum, a = {
                            hourMin: this.minHours,
                            hourMax: i(this.maxHours),
                            hourStep: this.opts.hoursStep,
                            hourValue: this.hours,
                            hourVisible: i(this.displayHours),
                            minMin: this.minMinutes,
                            minMax: i(this.maxMinutes),
                            minStep: this.opts.minutesStep,
                            minValue: i(this.minutes)
                        }, n = s.template(t, a);
                        this.$timepicker = e(n).appendTo(this.d.$datepicker), this.$ranges = e('[type="range"]', this.$timepicker), this.$hours = e('[name="hours"]', this.$timepicker), this.$minutes = e('[name="minutes"]', this.$timepicker), this.$hoursText = e(".datepicker--time-current-hours", this.$timepicker), this.$minutesText = e(".datepicker--time-current-minutes", this.$timepicker), this.d.ampm && (this.$ampm = e('<span class="datepicker--time-current-ampm">').appendTo(e(".datepicker--time-current", this.$timepicker)).html(this.dayPeriod), this.$timepicker.addClass("-am-pm-"))
                    }, _updateCurrentTime: function () {
                        var t = s.getLeadingZeroNum(this.displayHours), e = s.getLeadingZeroNum(this.minutes);
                        this.$hoursText.html(t), this.$minutesText.html(e), this.d.ampm && this.$ampm.html(this.dayPeriod)
                    }, _updateRanges: function () {
                        this.$hours.attr({
                            min: this.minHours,
                            max: this.maxHours
                        }).val(this.hours), this.$minutes.attr({
                            min: this.minMinutes,
                            max: this.maxMinutes
                        }).val(this.minutes)
                    }, _handleDate: function (t) {
                        this._setDefaultMinMaxTime(), t && (s.isSame(t, this.d.opts.minDate) ? this._setMinTimeFromDate(this.d.opts.minDate) : s.isSame(t, this.d.opts.maxDate) && this._setMaxTimeFromDate(this.d.opts.maxDate)), this._validateHoursMinutes(t)
                    }, update: function () {
                        this._updateRanges(), this._updateCurrentTime()
                    }, _getValidHoursFromDate: function (t, e) {
                        var i = t, a = t;
                        t instanceof Date && (i = s.getParsedDate(t), a = i.hours);
                        var n = e || this.d.ampm, h = "am";
                        if (n) switch (!0) {
                            case 0 == a:
                                a = 12;
                                break;
                            case 12 == a:
                                h = "pm";
                                break;
                            case a > 11:
                                a -= 12, h = "pm"
                        }
                        return {hours: a, dayPeriod: h}
                    }, set hours(t) {
                        this._hours = t;
                        var e = this._getValidHoursFromDate(t);
                        this.displayHours = e.hours, this.dayPeriod = e.dayPeriod
                    }, get hours() {
                        return this._hours
                    }, _onChangeRange: function (t) {
                        var i = e(t.target), s = i.attr("name");
                        this.d.timepickerIsActive = !0, this[s] = i.val(), this._updateCurrentTime(), this.d._trigger("timeChange", [this.hours, this.minutes]), this._handleDate(this.d.lastSelectedDate), this.update()
                    }, _onSelectDate: function (t, e) {
                        this._handleDate(e), this.update()
                    }, _onMouseEnterRange: function (t) {
                        var i = e(t.target).attr("name");
                        e(".datepicker--time-current-" + i, this.$timepicker).addClass("-focus-")
                    }, _onMouseOutRange: function (t) {
                        var i = e(t.target).attr("name");
                        this.d.inFocus || e(".datepicker--time-current-" + i, this.$timepicker).removeClass("-focus-")
                    }, _onMouseUpRange: function (t) {
                        this.d.timepickerIsActive = !1
                    }
                }
            }()
        }(window, jQuery);
        var jCalendar = function () {
            return {
                init: function () {
                    $(function () {
                        var $window = $(window);
                        var $dateInputs = $(".form-input_date");

                        function reloadDate() {
                            var today = new Date();
                            if ($window.width() > 990) {
                                $dateInputs.datepicker({
                                    position: 'bottom right', onSelect: function (fd, d, ins) {
                                        var $this = $(this);
                                        if (ins['$el'] && ins['$el'].length) {
                                            ins['$el'].trigger('change');
                                            ins['$el'].removeClass('form-input_date_uninit');
                                        }
                                        ins.hide();
                                    }
                                });
                                $dateInputs.filter('[name="enddaterecent"]').each(function () {
                                    var $this = $(this);
                                    $this.datepicker().data('datepicker').update({maxDate: new Date()}).selectDate(today);
                                });
                                $dateInputs.filter('[name="fromdaterecent"]').each(function () {
                                    var $this = $(this);
                                    today.setMonth(today.getMonth() - 1);
                                    $this.datepicker().data('datepicker').update({maxDate: new Date()}).selectDate(today);
                                });
                            } else {
                                $dateInputs.each(function () {
                                    $(this).datepicker().data('datepicker').destroy();
                                });
                            }
                        }

                        reloadDate();
                        $window.on('resize', function () {
                            reloadDate();
                        });
                    });
                }
            };
        };
        jCalendar().init();
        var menu = function () {
            var $menuMain = $('.menu_main');
            $menuMain.css('position', 'absolute');
            var menuHeight = $('.menu_main').outerHeight();
            $menuMain.css('position', 'static');
            var $body = $('body');

            function refresh() {
                if (window.innerWidth < 991) {
                    $('.menuModal').css('height', 0);
                    $menuMain.css('position', 'absolute');
                    menuHeight = $('.menu_main').outerHeight();
                    $menuMain.css('position', 'static');
                } else {
                    menuHeight = $('.menu_main').outerHeight();
                    $('.menuModal').removeClass("menuModal_OPEN").css('height', '');
                    $body.removeClass("Site_menuOPEN");
                    $('.menuTrigger').removeClass("menuTrigger_OPEN");
                }
            }

            return {
                init: function () {
                    if (window.innerWidth < 991) {
                        $(".menuModal").css('height', menuHeight);
                        $(".menuTrigger").each(function () {
                            $($(this).attr('href')).css('height', 0);
                        });
                    }
                    $(".menuTrigger").click(function (e) {
                        var $this = $(this), href = $this.attr("href");
                        if ($this.hasClass("menuTrigger_OPEN")) {
                            $body.removeClass("Site_menuOPEN");
                            $(href).removeClass("menuModal_OPEN").css('height', 0);
                            $this.removeClass("menuTrigger_OPEN");
                        } else {
                            $body.addClass("Site_menuOPEN");
                            $(href).addClass("menuModal_OPEN").css('height', menuHeight);
                            $this.addClass("menuTrigger_OPEN");
                        }
                        e.preventDefault();
                    });
                    $(window).on('resize', refresh);
                }
            };
        };
        menu().init();
        let modal = function () {
            let $trigger = $('.trigger'), $body = $('body'), $modal = $('.modal');
            let template = {img: (img) => '<div class="modal">' + '<div class="modal-window">' + '<a href="#" class="modal-close fa fa-close"></a>' + '<img src="' + img + '" />' + '</div>' + '</div>'};
            return {
                refresh: function () {
                }, init: function () {
                    function modalClick(e) {
                        let $target = $(e.target), $this = $(this);
                        if ($target.hasClass('modal-close')) {
                            $target = $this;
                        }
                        if ($this.is($target)) {
                            e.preventDefault();
                            $body.removeClass("Site_modalOPEN");
                            $this.removeClass("modal_OPEN");
                            $('[href="' + $this.attr('id') + '"]').removeClass("trigger_OPEN");
                        }
                    }

                    function triggerAction(e) {
                        e.preventDefault();
                        let $this = $(this), href = $this.attr("href"), $href = $(href);
                        if ($this.hasClass('trigger_dropdown')) {
                            $this.find('.modal').addClass('modal_OPEN');
                        } else {
                            if (!$(href).length) {
                                let $img = $(template.img($this.data('src')));
                                $img.attr('id', href.replace('#', ''));
                                $body.append($img);
                                $href = $(href);
                                $modal = $modal.add($href);
                                $href.click(modalClick);
                            }
                            $href.addClass("modal_OPEN");
                            $body.addClass("Site_modalOPEN");
                            $this.addClass("trigger_OPEN");
                        }
                    }

                    function windowClose(e) {
                        var $this = $(this);
                        $this.find('.modal-close').trigger('click');
                    }

                    $trigger.not('.trigger_dropdown').on('click', triggerAction);
                    $trigger.filter('.trigger_dropdown').on('mouseover', triggerAction);
                    $trigger.filter('.trigger_dropdown').on('mouseleave', windowClose);
                    $modal.click(modalClick);
                }
            };
        };
        modal().init();
        var search = function () {
            var $searchLink = $('.Header-searchLink');
            return {
                init: function () {
                    $searchLink.each(function () {
                        var $this = $(this);
                        $this.on('click', function () {
                            var $thisClick = $(this);
                            $thisClick.closest('.Header-searchWrap').toggleClass('Header-searchWrap_open');
                        });
                    });
                    $('.form_search').on('submit', function (e) {
                        var $this = $(this);
                        var $Middle = $this.closest('.Middle');
                        var pageSearch = $Middle.hasClass('Middle_search');
                        e.preventDefault();
                        if (pageSearch) {
                            function getData(address, data) {
                                $.ajax({
                                    url: address,
                                    type: 'GET',
                                    dataType: 'json',
                                    data: data,
                                    complete: function (result) {
                                        if (result.status === 200) {
                                            var data = '';
                                            result.responseJSON.books.forEach(function (book) {
                                                data += Card().bookTemplate(book);
                                            });
                                            var $Cards = $('.Cards_refresh');
                                            $Middle.find('.Middle-searchHeader').text('Найдено ' +
                                                result.responseJSON.count + ' книг' +
                                                (result.responseJSON.count % 10 < 10 && result.responseJSON.count % 10 > 20 ? (result.responseJSON.count % 10 === 1 ? 'a' : (result.responseJSON.count % 10 > 1 && result.responseJSON.count % 10 < 5 ? 'и' : '')) : ''))
                                            $Cards.find('.Card').remove();
                                            $Cards.prepend(data);
                                        } else {
                                            alert('Ошибка ' + result.status);
                                        }
                                    }
                                });
                            };getData('/api/search/page/' + $this.find('.search-input').val(), {
                                offset: 0,
                                limit: $this.data('searchlimit')
                            });
                        } else {
                            window.location = '\/search\/' + $this.find('.search-input').val();
                        }
                    });
                }
            };
        };
        search().init();
        var table = function () {
            return {
                init: function () {
                }
            };
        };
        table().init();
        var Author = function () {
            return {
                init: function () {
                }
            };
        };
        Author().init();
        var Authors = function () {
            return {
                init: function () {
                }
            };
        };
        Authors().init();
        var Card = function () {
            var bookTemplate = function (book) {
                var next = (book.authors.length > 1) ? ' и другие' : '';

                return '<div class="Card">' + '   <div class="Card-picture">' + '<a href="/api/books/' + book.slug + '">' + '<img src="' + book.image + '">\n' + '</a>' +
                    (book.discount ? '<div class="Card-sale">' + (book.discount * 100) + ' % скидка</div>' : '') +
                    (book.isBestseller ? '<div class="Card-ribbon"><span class="Card-ribbonText">Бестселлер</span></div>' : '') +
                    (book.status === 'PAID' ? '<a class="Card-status" title="Куплена">Куплена</a>\n' : '') +
                    (book.status === 'CART' ? '<a class="Card-status" title="В корзине">В корзине</a>\n' : '') +
                    (book.status === 'KEPT' ? '<a class="Card-status" title="Отложена"><img src="/assets/img/icons/heart.svg" alt="Отложена">Отложена</a>\n' : '') + '    </div>' + '              <div class="Card-content">\n' + '                <strong class="Card-title"><a href="/api/books/'+ book.slug +'">' + book.title + '</a>\n' + '                </strong>\n' + '                <div class="Card-description">' + '<a href="/api/author/' + book.authors[0].id + '">' + book.authors[0].fullName + next + '</a>                </div>\n' + '                <div class="Card-cost">' +
                    (book.discountPrice ? '<span class="Card-priceOld">₽' + book.price  + '</span>\n' : '') + '<span class="Card-price">₽' + book.discountPrice + '</span>\n' + '                </div>\n' + '              </div>\n' + '            </div>';
            }
            return {
                init: function () {
                    $('[data-refreshshow]').on('click change', function (e) {
                        var $this = $(this), data = $this.data('refreshshow');
                        e.preventDefault();

                        function getData(address, data, type) {
                            var scroll = $(window).scrollTop();
                            $.ajax({
                                url: address,
                                type: 'GET',
                                dataType: 'json',
                                data: data,
                                complete: function (result) {
                                    if (result.status === 200) {
                                        var data = '';
                                        result.responseJSON.books.forEach(function (book) {
                                            data += bookTemplate(book);
                                        });
                                        $this.data('refreshoffset', $this.data('refreshoffset') + $this.data('refreshlimit'));
                                        var $Cards = $('.Cards_refresh');
                                        if (type === 'changedate') {
                                            $Cards.find('.Card').remove();
                                        }
                                        if ($Cards.find('.Card').length) {
                                            $Cards.find('.Card').last().after(data);
                                        } else {
                                            $Cards.prepend(data);
                                        }
                                        $(window).scrollTop(scroll);
                                    } else {
                                        alert('Ошибка ' + result.status);
                                    }
                                }
                            });
                        }

                        switch (data) {
                            case'changedate':
                                if (e.type === 'change') {
                                    if ($this.is('[data-refreshfrom]')) {
                                        $this.data('refreshfrom', $this.val());
                                    }
                                    if ($this.is('[data-refreshto]')) {
                                        $this.data('refreshto', $this.val());
                                    }
                                    var $refreshoffset = $('[data-refreshoffset]');
                                    if (!$this.hasClass('form-input_date_uninit')) {
                                        $refreshoffset.data('refreshoffset', 0)
                                        getData('/api/books/recent/page', {
                                            from: $('[data-refreshfrom]').data('refreshfrom'),
                                            to: $('[data-refreshto]').data('refreshto'),
                                            offset: $this.data('refreshoffset'),
                                            limit: $this.data('refreshlimit')
                                        }, 'changedate');
                                        $refreshoffset.each(function () {
                                            var $this = $(this);
                                            $this.data('refreshoffset', $this.data('refreshlimit'));
                                        });
                                    }
                                }
                                break;
                            case'recent':
                                getData('/api/books/recent/page', {
                                    from: $('[data-refreshfrom]').data('refreshfrom'),
                                    to: $('[data-refreshto]').data('refreshto'),
                                    offset: function () {
                                        var oldOffset = $this.data('refreshoffset');
                                        if(oldOffset < 1){
                                            $this.data('refreshoffset', ++oldOffset);
                                        }else{
                                            $this.data('refreshoffset', ++oldOffset - $this.data('refreshlimit'));
                                        }
                                        return $this.data('refreshoffset');
                                    },
                                    limit: $this.data('refreshlimit')
                                });
                                break;
                            case'popular':
                                getData('/api/books/popular/page', {
                                    offset: function () {
                                        var oldOffset = $this.data('refreshoffset');
                                        if(oldOffset < 1){
                                            $this.data('refreshoffset', ++oldOffset);
                                        }else{
                                            $this.data('refreshoffset', ++oldOffset - $this.data('refreshlimit'));
                                        }
                                        return $this.data('refreshoffset');
                                    },
                                    limit: $this.data('refreshlimit')
                                });
                                break;
                            case'recently':
                                getData('/api/books/recently/page', {
                                    offset: function () {
                                        var oldOffset = $this.data('refreshoffset');
                                        if(oldOffset < 1){
                                            $this.data('refreshoffset', ++oldOffset);
                                        }else{
                                            $this.data('refreshoffset', ++oldOffset - $this.data('refreshlimit'));
                                        }
                                        return $this.data('refreshoffset');
                                    },
                                    limit: $this.data('refreshlimit')
                                });
                                break;
                            case'genre':
                                getData('/api/books/genre/' + $this.data('refreshid'), {
                                    offset: function () {
                                        var oldOffset = $this.data('refreshoffset');
                                        if (oldOffset >= 1) {
                                            oldOffset = oldOffset - $this.data('refreshlimit');
                                        }
                                        $this.data('refreshoffset', ++oldOffset);
                                        return $this.data('refreshoffset');
                                    },
                                    limit: $this.data('refreshlimit')
                                });
                                break;
                            case'genreType':
                                getData('/api/books/genreType/' + $this.data('refreshid'), {
                                    offset: function () {
                                        var oldOffset = $this.data('refreshoffset');
                                        if (oldOffset >= 1) {
                                            oldOffset = oldOffset - $this.data('refreshlimit');
                                        }
                                        $this.data('refreshoffset', ++oldOffset);
                                        return $this.data('refreshoffset');
                                    },
                                    limit: $this.data('refreshlimit')
                                });
                                break;
                            case'author':
                                getData('/api/books/author/page/' + $this.data('refreshid'), {
                                    offset: function () {
                                        var oldOffset = $this.data('refreshoffset');
                                        if(oldOffset < 1){
                                            $this.data('refreshoffset', ++oldOffset);
                                        }else{
                                            $this.data('refreshoffset', ++oldOffset - $this.data('refreshlimit'));
                                        }
                                        return $this.data('refreshoffset');
                                    },
                                    limit: $this.data('refreshlimit')
                                });
                                break;
                            case'tag':
                                getData('/api/books/tag/' + $this.data('refreshid'), {
                                    offset: function () {
                                        var oldOffset = $this.data('refreshoffset');
                                        if(oldOffset < 1){
                                            $this.data('refreshoffset', ++oldOffset);
                                        }else{
                                            $this.data('refreshoffset', ++oldOffset - $this.data('refreshlimit'));
                                        }
                                        return $this.data('refreshoffset');
                                    },
                                    limit: $this.data('refreshlimit')
                                });
                                break;
                            case'search':
                                getData('/api/search/page/' + $this.data('refreshquery'), {
                                    offset: function () {
                                        var oldOffset = $this.data('refreshoffset');
                                        if(oldOffset < 1){
                                            $this.data('refreshoffset', oldOffset + $this.data('refreshlimit'));
                                        }else{
                                            $this.data('refreshoffset', oldOffset);
                                        }
                                        return $this.data('refreshoffset');
                                    },
                                    limit: $this.data('refreshlimit')
                                });
                                break;
                            default:
                                break;
                        }
                    });
                }, bookTemplate: bookTemplate
            };
        };
        Card().init();
        var Cart = function () {
            return {
                init: function () {
                }
            };
        };
        Cart().init();
        var CartBlock = function () {
            return {
                init: function () {
                }
            };
        };
        CartBlock().init();
        var Comments = function () {
            return {
                init: function () {
                    $('[data-action="comments-show"]').on('click', function (e) {
                        e.preventDefault();
                        var $this = $(this), text = $this.data('text-alt'),
                            $comments = $this.prev('.Comments').find('.Comments-wrap_toggle');
                        $this.data('text-alt', $this.text());
                        $this.text(text);
                        $comments.toggleClass('Comments-wrap_HIDE');
                        $('.fixScrollBlock').trigger('render.airStickyBlock');
                    });
                    $('.Comments-addComment form').on('submit', function (e) {
                        e.preventDefault();
                        var $this = $(this), error = false, $validate = $this.find('[data-validate]');
                        $validate.each(function () {
                            var $this = $(this);
                            $this.trigger('blur');
                            if ($this.data('errorinput')) {
                                error = true;
                            }
                        });
                        if (!error) {
                            Login().postData('/api/books/bookReview/' + $this.data('bookid'), {
                                bookId: $this.data('bookid'),
                                text: $this.find('.Comments-review').val()
                            }, function (result) {
                                if (result.result) {
                                    $this.append('<div class="form-group">' + '<div class="Comments-successSend">' + 'Отзыв успешно сохранен' + '</div>' + '</div>')
                                } else {
                                    var $textarea = $this.find('.Comments-review');
                                    $textarea.addClass('form-textarea_error').after('<div class="form-error">' + result.error + '</div>');
                                }
                            })
                        }
                    });
                    $('[data-likeid]').on('click', function (e) {
                        var $this = $(this), data = {};
                        if ($this.hasClass('btn_like') && !$this.data('check')) {
                            data.value = 1;
                        } else if ($this.hasClass('btn_dislike') && !$this.data('check')) {
                            data.value = -1;
                        } else {
                            data.value = 0;
                        }
                        ;
                        data.reviewid = $this.data('likeid');
                        Login().postData('/api/books/rateBookReview/'+ $('[data-bookslug]').data('bookslug') , data, function (result) {
                            if (result.result) {
                                if ($this.data('btnradio')) {
                                    $('[data-btnradio="' + $this.data('btnradio') + '"]').each(function (e) {
                                        if ($(this).data('check') && !$(this).is($this)) {
                                            $(this).find('.btn-content').text(parseFloat($(this).text()) - 1);
                                        }
                                    });
                                }
                                if ($this.data('check')) {
                                    $this.find('.btn-content').text(parseFloat($this.text()) - 1);
                                } else {
                                    $this.find('.btn-content').text(parseFloat($this.text()) + 1);
                                }
                                ProductCard().shiftCheck($this);
                                window.location.reload(true);
                            }
                        });
                    });
                }
            };
        };
        Comments().init();
        var Contacts = function () {
            return {
                init: function () {
                    $('.Contacts-message').on('input paste', function () {
                        var $el = $(this), offset = $el.innerHeight() - $el.height();
                        if ($el.innerHeight < this.scrollHeight) {
                            $el.height(this.scrollHeight - offset);
                        } else {
                            $el.height(1);
                            $el.height(this.scrollHeight - offset);
                        }
                    });
                }
            };
        };
        Contacts().init();
        var Documents = function () {
            return {
                init: function () {
                }
            };
        };
        Documents().init();
        var FAQ = function () {
            return {
                init: function () {
                }
            };
        };
        FAQ().init();
        var HideBlock = function () {
            var $HideBlock = $('.HideBlock');
            var $trigger = $HideBlock.find('.HideBlock-trigger');
            $HideBlock.each(function () {
                var $this = $(this);
                var $content = $this.find('.HideBlock-content');
                $content.css('height', $content.outerHeight());
                $this.addClass('HideBlock_CLOSE');
            });
            return {
                init: function () {
                    $trigger.on('click', function (e) {
                        e.preventDefault();
                        var $this = $(this);
                        var $parent = $this.closest($HideBlock);
                        if ($parent.hasClass('HideBlock_CLOSE')) {
                            $HideBlock.addClass('HideBlock_CLOSE');
                            $parent.removeClass('HideBlock_CLOSE');
                        } else {
                            $parent.addClass('HideBlock_CLOSE');
                        }
                    });
                    $HideBlock.eq(0).find($trigger).trigger('click');
                }
            };
        };
        HideBlock().init();
        var Login = function () {
            var $Login = $('.Login');
            var $Registration = $Login.filter('.Login_registration');
            var $LoginStep = $('.Login-step');
            var $sendButton = $('button[name="sendauth"]');
            var $entryButton = $('button[name="toComeInPhone"], button[name="toComeInMail"]');

            function postData(address, data, cb, cbErr) {
                // var token = $('#_csrf').attr('content');
                // var header = $('#_csrf_header').attr('content');

                $.ajax({
                    url: address,
                    type: 'POST',
                    dataType: 'json',
                    data: JSON.stringify(data),
                    // beforeSend: function (xhr) {
                    //     xhr.setRequestHeader(header, token);
                    // },
                    headers:{
                        'Accept': 'application/json',
                        'Content-Type': 'application/json'
                    },
                    complete: function (result) {
                        if (result.status === 200) {
                            cb(result.responseJSON);
                        } else {

                            if (cbErr) {
                                cbErr(result.responseJSON);
                                alert(result);
                            }
                        }
                    }
                });
            }
            return {
                init: function () {
                    var $regCode = $Registration.find('.form-input_code');
                    $('#submitPhone').on('click', function () {
                        var $this = $(this), $row = $this.closest('.form-group_row'),
                            data = {contact: $row.find('.form-input_phone').val()};
                        postData('/requestContactConfirmation', data, function (result) {
                            if (result.result) {
                                $this.after('<div class="form-info">Вам направлен код подтверждения по SMS</div>')
                                $this.attr('disabled', 'disabled');
                                $row.addClass('form-group_confirm');
                                $row.find($regCode).prop('disabled', false);
                                $row.find('.form-error').remove();
                            } else {
                                $row.find('.form-input[type="text"]').addClass('form-input_error').after('<div class="form-error">' + result.error + '</div>');
                            }
                        })
                    });
                    $('#submitMail').on('click', function () {
                        var $this = $(this), $row = $this.closest('.form-group_row'),
                            data = {contact: $row.find('.form-input_mail').val()};
                        postData('/requestEmailConfirmation', data, function (result) {
                            if (result.result) {
                                $this.after('<div class="form-info">Вам направлен код подтверждения в письме на почту</div>')
                                $this.attr('disabled', 'disabled');
                                $row.addClass('form-group_confirm');
                                $row.find('.form-error').remove();
                            } else {
                                $row.find('.form-input[type="text"]').addClass('form-input_error').after('<div class="form-error">' + result.error + '</div>');
                            }
                        })
                    });
                    $Registration.find('.form-input_code').on('focus', function () {
                        var $this = $(this), $row = $this.closest('.form-group_row'), $but = $row.find('.form-btn');
                        $but.next('.form-info').remove();
                    });
                    $Registration.find('.form-input_code').on('blur', function () {
                        var $this = $(this), $row = $this.closest('.form-group_row'), data = {
                            contact: $row.find('.form-input_mail, .form-input_phone').val(),
                            code: $row.find('.form-input_code').val()
                        }, $but = $row.find('.form-btn');
                        if (!$this.hasClass('form-input_error')) {
                            postData('/approveContact', data, function (result) {
                                if (result.result) {
                                    $this.attr('disabled', 'disabled');
                                    $but.attr('disabled', 'disabled');
                                    $but.after('<div class="form-info">Код успешно подтвержден</div>');
                                    $this.trigger('change');
                                } else {
                                    if (result) {
                                        //  $row.removeClass('form-group_confirm');
                                        $this.val('');
                                        $but.prop('disabled', false);
                                        $row.find('.form-input_mail, .form-input_phone').addClass('form-input_error').after('<div class="form-error">' + 'Wrong code or phone number!' + '</div>');
                                    } else {
                                        $this.addClass('form-input_error').after('<div class="form-error">' + result.error + '</div>');
                                    }
                                }
                            }, function (result) {
                                if (!result.result) {
                                    $but.prop('disabled', false);
                                }
                            })
                        }
                    })
                    $Registration.find('.form-input').on('blur', function () {
                        $(this).trigger('change');
                    });
                    $Registration.find('.form-input').on('change', function () {
                        var $this = $(this), $row = $this.closest('.form-group_row'), $but = $row.find('.form-btn'),
                            inputs = Array.from($this.closest('form').find('.form-input')),
                            $butReg = $Registration.find('[name="registration"');
                        if (inputs.every(function (item) {
                            var $this = $(item);
                            if ($this.hasClass('form-input_code')) {
                                if ($this.prop('disabled')) {
                                    return true;
                                } else {
                                    return false;
                                }
                            } else {
                                return (!$(item).hasClass('form-input_error') && $(item).val())
                            }
                        })) {
                            $butReg.prop('disabled', false);
                        } else {
                            $butReg.attr('disabled', 'disabled');
                        }
                        ;
                        if (($this.hasClass('form-input_phone') || $this.hasClass('form-input_mail')) && $this.val() && !$this.hasClass('form-input_error')) {
                            $but.prop('disabled', false);
                            $but.next('.form-info').remove();
                            $row.find($regCode).prop('disabled', false);
                        } else {
                            $but.attr('disabled', 'disabled');
                        }
                    })
                    $('.Login-type input').on('change', function () {
                        var $this = $(this);
                        var $mail = $('.form-input[name="mail"]');
                        var $phone = $('.form-input[name="phone"]');
                        var $thisStep = $this.closest($LoginStep);
                        $thisStep.find('[data-validate]').each(function () {
                            var $this = $(this);
                            $this.next('.form-error').remove();
                            $this.removeClass('form-input_error');
                            $this.removeClass('form-textarea_error');
                            $this.data('errorinput', false);
                        });
                        if ($this.attr("name") === 'typeauth') {
                            switch ($this.val()) {
                                case'phonetype':
                                    $phone.removeClass('form-input_hide').prop('disabled', false);
                                    $mail.addClass('form-input_hide').prop('disabled', true);
                                    $sendButton.data('type', 'phone');
                                    break;
                                case'mailtype':
                                    $mail.removeClass('form-input_hide').prop('disabled', false);
                                    $phone.addClass('form-input_hide').prop('disabled', true);
                                    $sendButton.data('type', 'mail');
                                    break;
                            }
                        }
                    });
                    $('.Login-type').eq(0).find('input').trigger('change');
                    $sendButton.on('click', function (e) {
                        e.preventDefault();
                        var $this = $(this);
                        var $thisStep = $this.closest($LoginStep), $validate = $thisStep.find('[data-validate]'),
                            error = false;
                        $validate.each(function () {
                            var $this = $(this);
                            $this.trigger('blur');
                            if ($this.data('errorinput')) {
                                error = true;
                            }
                        });
                        if ($this.is('[data-send]')) {
                            var contact = $thisStep.find('.form-input_phone, .form-input_mail').not('.form-input_hide').val();
                            $this.data('send', {contact: contact});
                            if (!error) {
                                postData('/requestContactConfirmation', $this.data('send'), function (result) {
                                    if (result.result) {
                                        $thisStep.addClass('Login-step_hide');
                                        $thisStep.addClass('Login-step_completed');
                                        $LoginStep.find('[data-send]').data('send', {contact: contact});
                                        switch ($this.data('type')) {
                                            case'phone':
                                                $LoginStep.filter('[data-type="phone"]').removeClass('Login-step_hide');
                                                break;
                                            case'mail':
                                                $LoginStep.filter('[data-type="mail"]').removeClass('Login-step_hide');
                                                break;
                                        }
                                    } else {
                                        $thisStep.find('.form-input:not(.form-input_hide)[type="text"]').addClass('form-input_error').after('<div class="form-error">' + result.error + '</div>');
                                    }
                                })
                            }
                        }
                    });
                    $entryButton.on('click', function (e) {
                        e.preventDefault();
                        var $this = $(this);
                        var $thisStep = $this.closest($LoginStep), $validate = $thisStep.find('[data-validate]'),
                            error = false;
                        $validate.each(function () {
                            var $this = $(this);
                            $this.trigger('blur');
                            if ($this.data('errorinput')) {
                                error = true;
                            }
                        });
                        if ($this.is('[data-send]')) {
                            var code = $thisStep.find('[name="phonecode"], [name="mailcode"]').not('.form-input_hide').val();
                            var send = $this.data('send');
                            $this.data('send', {code: code, contact: send.contact});
                            if (!error && $this.attr('id') != 'toComeInPhone') {
                                postData('/login', $this.data('send'), function (result) {
                                    if (result != null && result.result) {
                                        $LoginStep.find('[data-send]').data('send', {
                                            code: code,
                                            contact: send.contact
                                        });
                                        window.location.href = '/my';
                                    } else {
                                        if (result) {
                                            $thisStep.addClass('Login-step_hide');
                                            $thisStep.find('.form-input:not(.form-input_hide)[type="text"]').val('');
                                            $thisStep = $thisStep.prevAll('.Login-step_completed').last();
                                            $thisStep.removeClass('Login-step_hide');
                                            $thisStep.find('.form-input:not(.form-input_hide)[type="text"]').addClass('form-input_error').after('<div class="form-error">' + 'Пользователь не найден или неверный логин\/пароль!' + '</div>');
                                        } else {
                                            $thisStep.find('.form-input:not(.form-input_hide)[type="text"]').addClass('form-input_error').after('<div class="form-error">' + 'Пользователь не найден или неверный логин\/пароль!' + '</div>');
                                        }
                                    }
                                })
                            }
                            else {

                                postData('/login-by-phone-number', $this.data('send'), function (result) {
                                    if (result != null && result.result) {
                                        $LoginStep.find('[data-send]').data('send', {
                                            code: code,
                                            contact: send.contact
                                        });
                                        window.location.href = '/my';
                                    } else {
                                        if (result) {
                                            $thisStep.addClass('Login-step_hide');
                                            $thisStep.find('.form-input:not(.form-input_hide)[type="text"]').val('');
                                            $thisStep = $thisStep.prevAll('.Login-step_completed').last();
                                            $thisStep.removeClass('Login-step_hide');
                                            $thisStep.find('.form-input:not(.form-input_hide)[type="text"]').addClass('form-input_error').after('<div class="form-error">' + 'Пользователь не найден или неверный логин\/пароль!'+ '</div>');
                                        } else {
                                            $thisStep.find('.form-input:not(.form-input_hide)[type="text"]').addClass('form-input_error').after('<div class="form-error">' + 'Пользователь не найден или неверный логин\/пароль!' + '</div>');
                                        }
                                    }
                                })

                            }
                        }
                    });
                }, postData: postData
            };
        };
        Login().init();
        var Middle = function () {
            return {
                init: function () {
                }
            };
        };
        Middle().init();
        var Product = function () {
            return {
                init: function () {
                }
            };
        };
        Product().init();
        var ProductCard = function () {
            var $picts = $('.ProductCard-pict');
            var $photo = $('.ProductCard-photo');

            function shiftCheck($element, wave) {
                var text = '', check = $element.data('check');
                text = $element.find('.btn-content').text();
                if ($element.data('alttext')) {
                    $element.find('.btn-content').text($element.data('alttext'));
                    $element.data('alttext', text);
                }
                check = !check;
                $element.data('check', check);
                if (check) {
                    $element.addClass('btn_check');
                } else {
                    $element.removeClass('btn_check');
                }
                if (!wave) {
                    $element.trigger('changeCheck');
                }
            }

            return {
                init: function () {
                    $picts.on('click', function (e) {
                        e.preventDefault();
                        var $this = $(this);
                        var href = $this.attr('href');
                        $photo.empty();
                        $photo.append('<img src="' + href + '" />');
                        $picts.removeClass('ProductCard-pict_ACTIVE');
                        $this.addClass('ProductCard-pict_ACTIVE');
                    });
                    var $btnCheck = $('[data-btntype="check"]');
                    $btnCheck.on('click', function (e) {
                        var $this = $(this);
                        if (!$this.is('[data-sendstatus]') && !$this.is('[data-likeid]')) {
                            shiftCheck($this);
                        }
                    });
                    $btnCheck.on('changeCheck', function () {
                        var $this = $(this);
                        if ($this.data('btnradio')) {
                            $('[data-btnradio="' + $this.data('btnradio') + '"]').each(function (e) {
                                if ($(this).data('check') && !$(this).is($this)) {
                                    shiftCheck($(this), true);
                                }
                            });
                        }
                    });
                    var $btnSend = $('[data-sendstatus]');
                    $btnSend.on('click', function (e) {
                        var $this = $(this), status = 'UNLINK', $cart = $this.closest('.Cart');
                        if (!$this.data('check')) {
                            status = $this.data('sendstatus');
                        }
                        if ($cart.length) {
                            e.preventDefault();
                        }
                        if ($this.data('sendstatus') === 'ARCHIVED' && $this.data('check')) {
                            Login().postData('/api/books/changeBookStatus/' +  $this.data('bookid'), {
                                booksIds: $this.data('bookid'),
                                status: 'PAID'
                            }, function (result) {
                                if ($this.is('[data-btntype="check"]')) {
                                    shiftCheck($this);
                                }
                            });
                        } else {
                            Login().postData('/api/books/changeBookStatus/' + $this.data('bookid'), {
                                booksIds: $this.data('bookid'),
                                status: status
                            }, function (result) {
                                if (result.result) {
                                    $this.next('.btnError').remove();
                                    if ($cart.length) {
                                        var price = 0, priceOld = 0, booksId = [];
                                        $this.closest('.Cart-product').remove();
                                        $cart.find('.Cart-product').each(function () {
                                            var $this = $(this);
                                            price += parseFloat($this.find('.Cart-price:not(.Cart-price_old)').text());
                                            priceOld += parseFloat($this.find('.Cart-price_old').text());
                                            booksId.push($this.find('[data-sendstatus="CART"]').data('bookid'));
                                        });
                                        $cart.find('.Cart-total .Cart-price').not('.Cart-price_old').text(price + ' р.');
                                        $cart.find('.Cart-total .Cart-price_old').text(priceOld + ' р.');
                                        if ($cart.hasClass('Cart_postponed')) {
                                            $cart.find('.Cart-buyAll').data('bookid', booksId);
                                        }
                                        if (!$cart.find('.Cart-product').length) {
                                            if ($cart.hasClass('Cart_postponed')) {
                                                $cart.prepend('<div class="Cart-messageInfo">Отложенных книг нет</div>');
                                            } else {
                                                $cart.prepend('<div class="Cart-messageInfo">Корзина пуста</div>');
                                            }
                                            $cart.find('.Cart-total .btn').attr('disabled', 'disabled');
                                        }
                                        if ($this.hasClass('Cart-buyAll')) {
                                            window.location.href = '/api/books/cart';
                                        }
                                    }
                                    if ($this.is('[data-btntype="check"]')) {
                                        shiftCheck($this);
                                        if ($this.data('btnradio')) {
                                            $('[data-btnradio="' + $this.data('btnradio') + '"]').next('.btnError').remove();
                                        }
                                    }
                                } else {
                                    $this.after('<div class="btnError">' + result.error + '</div>');
                                }
                            })
                            window.location.reload(true);
                        }
                    });
                    var $ProductAssesment = $('.ProductCard-assessment');
                    $ProductAssesment.find('.Rating input').on('change', function (e) {
                        var $this = $(this), check = $this.prop('checked');
                        Login().postData('/api/books/rateBook/' + $this.data('bookid'), {
                            bookId: $this.closest('.Rating').data('bookid'),
                            value: (check ? $this.val() : 0)
                        }, function (result) {
                            if (!$this.closest('.Rating').find('.Rating-title').length) {
                                $this.closest('.Rating').append('<span class="Rating-title"></span>');
                            }
                            var $title = $this.closest('.Rating').find('.Rating-title');
                            if (result.result) {
                                $title.addClass('Rating-title_success').text('Рейтинг успешно изменен');

                            } else {
                                $title.addClass('Rating-title_error').text('Возникла ошибка');
                                $this.prop('checked', false);
                            }
                            setTimeout(function () {
                                $title.text('');
                            }, 2500);
                        });
                        window.location.reload(true);
                    });
                }, shiftCheck: shiftCheck
            };
        };
        ProductCard().init();
        var Profile = function () {
            var $avatar = $('.Profile-avatar');
            return {
                init: function () {
                    var $avatarfile = $avatar.find('.Profile-file');

                    function readURL(input) {
                        if (input.files && input.files[0]) {
                            var file = input.files[0], ext = 'неизвестно';
                            ext = file.name.split('.').pop();
                            if (ext === 'png' || ext === 'jpg' || ext === 'gif') {
                                var reader = new FileReader();
                                reader.onload = function (e) {
                                    $(input).closest($avatar).find('.Profile-img img').attr('src', e.target.result);
                                }
                                reader.readAsDataURL(file);
                                return true;
                            }
                            return false;
                        }
                    }

                    $('input[name="mail"], input[name="phone"]').on('change', function () {
                        var $this = $(this);
                        $this.next('.Profile-btn_confirm').show(0);
                    });
                    $avatarfile.change(function () {
                        var $thisAvatar = $(this).closest($avatar);
                        if (readURL(this)) {
                            $thisAvatar.removeClass('Profile-avatar_noimg');
                            $thisAvatar.next('.form-error').remove();
                            $thisAvatar.find('input[type="file"]').data('errorinput', false);
                        } else {
                            if (!$thisAvatar.next('.form-error').length) {
                                $thisAvatar.find('input[type="file"]').data('errorinput', true);
                                $thisAvatar.after('<div class="form-error">Для загрузки допустимы лишь картинки с расширением png, jpg, gif</div>');
                            }
                        }
                        ;
                    });
                }
            };
        };
        Profile().init();
        var Rating = function () {
            return {
                init: function () {
                    $('.Rating_input:not(.Rating_inputClick)').on('click', function () {
                        $(this).addClass('Rating_inputClick');
                    });
                }
            };
        };
        Rating().init();
        var Section = function () {
            return {
                init: function () {
                }
            };
        };
        Section().init();
        !function (i) {
            "use strict";
            "function" == typeof define && define.amd ? define(["jquery"], i) : "undefined" != typeof exports ? module.exports = i(require("jquery")) : i(jQuery)
        }(function (i) {
            "use strict";
            var e = window.Slick || {};
            (e = function () {
                var e = 0;
                return function (t, o) {
                    var s, n = this;
                    n.defaults = {
                        accessibility: !0,
                        adaptiveHeight: !1,
                        appendArrows: i(t),
                        appendDots: i(t),
                        arrows: !0,
                        asNavFor: null,
                        prevArrow: '<button class="slick-prev" aria-label="Previous" type="button">Previous</button>',
                        nextArrow: '<button class="slick-next" aria-label="Next" type="button">Next</button>',
                        autoplay: !1,
                        autoplaySpeed: 3e3,
                        centerMode: !1,
                        centerPadding: "50px",
                        cssEase: "ease",
                        customPaging: function (e, t) {
                            return i('<button type="button" />').text(t + 1)
                        },
                        dots: !1,
                        dotsClass: "slick-dots",
                        draggable: !0,
                        easing: "linear",
                        edgeFriction: .35,
                        fade: !1,
                        focusOnSelect: !1,
                        focusOnChange: !1,
                        infinite: !0,
                        initialSlide: 0,
                        lazyLoad: "ondemand",
                        mobileFirst: !1,
                        pauseOnHover: !0,
                        pauseOnFocus: !0,
                        pauseOnDotsHover: !1,
                        respondTo: "window",
                        responsive: null,
                        rows: 1,
                        rtl: !1,
                        slide: "",
                        slidesPerRow: 1,
                        slidesToShow: 1,
                        slidesToScroll: 1,
                        speed: 500,
                        swipe: !0,
                        swipeToSlide: !1,
                        touchMove: !0,
                        touchThreshold: 5,
                        useCSS: !0,
                        useTransform: !0,
                        variableWidth: !1,
                        vertical: !1,
                        verticalSwiping: !1,
                        waitForAnimate: !0,
                        zIndex: 1e3
                    }, n.initials = {
                        animating: !1,
                        dragging: !1,
                        autoPlayTimer: null,
                        currentDirection: 0,
                        currentLeft: null,
                        currentSlide: 0,
                        direction: 1,
                        $dots: null,
                        listWidth: null,
                        listHeight: null,
                        loadIndex: 0,
                        $nextArrow: null,
                        $prevArrow: null,
                        scrolling: !1,
                        slideCount: null,
                        slideWidth: null,
                        $slideTrack: null,
                        $slides: null,
                        sliding: !1,
                        slideOffset: 0,
                        swipeLeft: null,
                        swiping: !1,
                        $list: null,
                        touchObject: {},
                        transformsEnabled: !1,
                        unslicked: !1
                    }, i.extend(n, n.initials), n.activeBreakpoint = null, n.animType = null, n.animProp = null, n.breakpoints = [], n.breakpointSettings = [], n.cssTransitions = !1, n.focussed = !1, n.interrupted = !1, n.hidden = "hidden", n.paused = !0, n.positionProp = null, n.respondTo = null, n.rowCount = 1, n.shouldClick = !0, n.$slider = i(t), n.$slidesCache = null, n.transformType = null, n.transitionType = null, n.visibilityChange = "visibilitychange", n.windowWidth = 0, n.windowTimer = null, s = i(t).data("slick") || {}, n.options = i.extend({}, n.defaults, o, s), n.currentSlide = n.options.initialSlide, n.originalSettings = n.options, void 0 !== document.mozHidden ? (n.hidden = "mozHidden", n.visibilityChange = "mozvisibilitychange") : void 0 !== document.webkitHidden && (n.hidden = "webkitHidden", n.visibilityChange = "webkitvisibilitychange"), n.autoPlay = i.proxy(n.autoPlay, n), n.autoPlayClear = i.proxy(n.autoPlayClear, n), n.autoPlayIterator = i.proxy(n.autoPlayIterator, n), n.changeSlide = i.proxy(n.changeSlide, n), n.clickHandler = i.proxy(n.clickHandler, n), n.selectHandler = i.proxy(n.selectHandler, n), n.setPosition = i.proxy(n.setPosition, n), n.swipeHandler = i.proxy(n.swipeHandler, n), n.dragHandler = i.proxy(n.dragHandler, n), n.keyHandler = i.proxy(n.keyHandler, n), n.instanceUid = e++, n.htmlExpr = /^(?:\s*(<[\w\W]+>)[^>]*)$/, n.registerBreakpoints(), n.init(!0)
                }
            }()).prototype.activateADA = function () {
                this.$slideTrack.find(".slick-active").attr({"aria-hidden": "false"}).find("a, input, button, select").attr({tabindex: "0"})
            }, e.prototype.addSlide = e.prototype.slickAdd = function (e, t, o) {
                var s = this;
                if ("boolean" == typeof t) o = t, t = null; else if (t < 0 || t >= s.slideCount) return !1;
                s.unload(), "number" == typeof t ? 0 === t && 0 === s.$slides.length ? i(e).appendTo(s.$slideTrack) : o ? i(e).insertBefore(s.$slides.eq(t)) : i(e).insertAfter(s.$slides.eq(t)) : !0 === o ? i(e).prependTo(s.$slideTrack) : i(e).appendTo(s.$slideTrack), s.$slides = s.$slideTrack.children(this.options.slide), s.$slideTrack.children(this.options.slide).detach(), s.$slideTrack.append(s.$slides), s.$slides.each(function (e, t) {
                    i(t).attr("data-slick-index", e)
                }), s.$slidesCache = s.$slides, s.reinit()
            }, e.prototype.animateHeight = function () {
                var i = this;
                if (1 === i.options.slidesToShow && !0 === i.options.adaptiveHeight && !1 === i.options.vertical) {
                    var e = i.$slides.eq(i.currentSlide).outerHeight(!0);
                    i.$list.animate({height: e}, i.options.speed)
                }
            }, e.prototype.animateSlide = function (e, t) {
                var o = {}, s = this;
                s.animateHeight(), !0 === s.options.rtl && !1 === s.options.vertical && (e = -e), !1 === s.transformsEnabled ? !1 === s.options.vertical ? s.$slideTrack.animate({left: e}, s.options.speed, s.options.easing, t) : s.$slideTrack.animate({top: e}, s.options.speed, s.options.easing, t) : !1 === s.cssTransitions ? (!0 === s.options.rtl && (s.currentLeft = -s.currentLeft), i({animStart: s.currentLeft}).animate({animStart: e}, {
                    duration: s.options.speed,
                    easing: s.options.easing,
                    step: function (i) {
                        i = Math.ceil(i), !1 === s.options.vertical ? (o[s.animType] = "translate(" + i + "px, 0px)", s.$slideTrack.css(o)) : (o[s.animType] = "translate(0px," + i + "px)", s.$slideTrack.css(o))
                    },
                    complete: function () {
                        t && t.call()
                    }
                })) : (s.applyTransition(), e = Math.ceil(e), !1 === s.options.vertical ? o[s.animType] = "translate3d(" + e + "px, 0px, 0px)" : o[s.animType] = "translate3d(0px," + e + "px, 0px)", s.$slideTrack.css(o), t && setTimeout(function () {
                    s.disableTransition(), t.call()
                }, s.options.speed))
            }, e.prototype.getNavTarget = function () {
                var e = this, t = e.options.asNavFor;
                return t && null !== t && (t = i(t).not(e.$slider)), t
            }, e.prototype.asNavFor = function (e) {
                var t = this.getNavTarget();
                null !== t && "object" == typeof t && t.each(function () {
                    var t = i(this).slick("getSlick");
                    t.unslicked || t.slideHandler(e, !0)
                })
            }, e.prototype.applyTransition = function (i) {
                var e = this, t = {};
                !1 === e.options.fade ? t[e.transitionType] = e.transformType + " " + e.options.speed + "ms " + e.options.cssEase : t[e.transitionType] = "opacity " + e.options.speed + "ms " + e.options.cssEase, !1 === e.options.fade ? e.$slideTrack.css(t) : e.$slides.eq(i).css(t)
            }, e.prototype.autoPlay = function () {
                var i = this;
                i.autoPlayClear(), i.slideCount > i.options.slidesToShow && (i.autoPlayTimer = setInterval(i.autoPlayIterator, i.options.autoplaySpeed))
            }, e.prototype.autoPlayClear = function () {
                var i = this;
                i.autoPlayTimer && clearInterval(i.autoPlayTimer)
            }, e.prototype.autoPlayIterator = function () {
                var i = this, e = i.currentSlide + i.options.slidesToScroll;
                i.paused || i.interrupted || i.focussed || (!1 === i.options.infinite && (1 === i.direction && i.currentSlide + 1 === i.slideCount - 1 ? i.direction = 0 : 0 === i.direction && (e = i.currentSlide - i.options.slidesToScroll, i.currentSlide - 1 == 0 && (i.direction = 1))), i.slideHandler(e))
            }, e.prototype.buildArrows = function () {
                var e = this;
                !0 === e.options.arrows && (e.$prevArrow = i(e.options.prevArrow).addClass("slick-arrow"), e.$nextArrow = i(e.options.nextArrow).addClass("slick-arrow"), e.slideCount > e.options.slidesToShow ? (e.$prevArrow.removeClass("slick-hidden").removeAttr("aria-hidden tabindex"), e.$nextArrow.removeClass("slick-hidden").removeAttr("aria-hidden tabindex"), e.htmlExpr.test(e.options.prevArrow) && e.$prevArrow.prependTo(e.options.appendArrows), e.htmlExpr.test(e.options.nextArrow) && e.$nextArrow.appendTo(e.options.appendArrows), !0 !== e.options.infinite && e.$prevArrow.addClass("slick-disabled").attr("aria-disabled", "true")) : e.$prevArrow.add(e.$nextArrow).addClass("slick-hidden").attr({
                    "aria-disabled": "true",
                    tabindex: "-1"
                }))
            }, e.prototype.buildDots = function () {
                var e, t, o = this;
                if (!0 === o.options.dots) {
                    for (o.$slider.addClass("slick-dotted"), t = i("<ul />").addClass(o.options.dotsClass), e = 0; e <= o.getDotCount(); e += 1) t.append(i("<li />").append(o.options.customPaging.call(this, o, e)));
                    o.$dots = t.appendTo(o.options.appendDots), o.$dots.find("li").first().addClass("slick-active")
                }
            }, e.prototype.buildOut = function () {
                var e = this;
                e.$slides = e.$slider.children(e.options.slide + ":not(.slick-cloned)").addClass("slick-slide"), e.slideCount = e.$slides.length, e.$slides.each(function (e, t) {
                    i(t).attr("data-slick-index", e).data("originalStyling", i(t).attr("style") || "")
                }), e.$slider.addClass("slick-slider"), e.$slideTrack = 0 === e.slideCount ? i('<div class="slick-track"/>').appendTo(e.$slider) : e.$slides.wrapAll('<div class="slick-track"/>').parent(), e.$list = e.$slideTrack.wrap('<div class="slick-list"/>').parent().width('100%'), e.$slideTrack.css("opacity", 0), !0 !== e.options.centerMode && !0 !== e.options.swipeToSlide || (e.options.slidesToScroll = 1), i("img[data-lazy]", e.$slider).not("[src]").addClass("slick-loading"), e.setupInfinite(), e.buildArrows(), e.buildDots(), e.updateDots(), e.setSlideClasses("number" == typeof e.currentSlide ? e.currentSlide : 0), !0 === e.options.draggable && e.$list.addClass("draggable")
            }, e.prototype.buildRows = function () {
                var i, e, t, o, s, n, r, l = this;
                if (o = document.createDocumentFragment(), n = l.$slider.children(), l.options.rows > 1) {
                    for (r = l.options.slidesPerRow * l.options.rows, s = Math.ceil(n.length / r), i = 0; i < s; i++) {
                        var d = document.createElement("div");
                        for (e = 0; e < l.options.rows; e++) {
                            var a = document.createElement("div");
                            for (t = 0; t < l.options.slidesPerRow; t++) {
                                var c = i * r + (e * l.options.slidesPerRow + t);
                                n.get(c) && a.appendChild(n.get(c))
                            }
                            d.appendChild(a)
                        }
                        o.appendChild(d)
                    }
                    l.$slider.empty().append(o), l.$slider.children().children().children().css({
                        width: 100 / l.options.slidesPerRow + "%",
                        display: "inline-block"
                    })
                }
            }, e.prototype.checkResponsive = function (e, t) {
                var o, s, n, r = this, l = !1, d = r.$slider.width(), a = window.innerWidth || i(window).width();
                if ("window" === r.respondTo ? n = a : "slider" === r.respondTo ? n = d : "min" === r.respondTo && (n = Math.min(a, d)), r.options.responsive && r.options.responsive.length && null !== r.options.responsive) {
                    s = null;
                    for (o in r.breakpoints) r.breakpoints.hasOwnProperty(o) && (!1 === r.originalSettings.mobileFirst ? n < r.breakpoints[o] && (s = r.breakpoints[o]) : n > r.breakpoints[o] && (s = r.breakpoints[o]));
                    null !== s ? null !== r.activeBreakpoint ? (s !== r.activeBreakpoint || t) && (r.activeBreakpoint = s, "unslick" === r.breakpointSettings[s] ? r.unslick(s) : (r.options = i.extend({}, r.originalSettings, r.breakpointSettings[s]), !0 === e && (r.currentSlide = r.options.initialSlide), r.refresh(e)), l = s) : (r.activeBreakpoint = s, "unslick" === r.breakpointSettings[s] ? r.unslick(s) : (r.options = i.extend({}, r.originalSettings, r.breakpointSettings[s]), !0 === e && (r.currentSlide = r.options.initialSlide), r.refresh(e)), l = s) : null !== r.activeBreakpoint && (r.activeBreakpoint = null, r.options = r.originalSettings, !0 === e && (r.currentSlide = r.options.initialSlide), r.refresh(e), l = s), e || !1 === l || r.$slider.trigger("breakpoint", [r, l])
                }
            }, e.prototype.changeSlide = function (e, t) {
                var o, s, n, r = this, l = i(e.currentTarget);
                switch (l.is("a") && e.preventDefault(), l.is("li") || (l = l.closest("li")), n = r.slideCount % r.options.slidesToScroll != 0, o = n ? 0 : (r.slideCount - r.currentSlide) % r.options.slidesToScroll, e.data.message) {
                    case"previous":
                        s = 0 === o ? r.options.slidesToScroll : r.options.slidesToShow - o, r.slideCount > r.options.slidesToShow && r.slideHandler(r.currentSlide - s, !1, t);
                        break;
                    case"next":
                        s = 0 === o ? r.options.slidesToScroll : o, r.slideCount > r.options.slidesToShow && r.slideHandler(r.currentSlide + s, !1, t);
                        break;
                    case"index":
                        var d = 0 === e.data.index ? 0 : e.data.index || l.index() * r.options.slidesToScroll;
                        r.slideHandler(r.checkNavigable(d), !1, t), l.children().trigger("focus");
                        break;
                    default:
                        return
                }
            }, e.prototype.checkNavigable = function (i) {
                var e, t;
                if (e = this.getNavigableIndexes(), t = 0, i > e[e.length - 1]) i = e[e.length - 1]; else for (var o in e) {
                    if (i < e[o]) {
                        i = t;
                        break
                    }
                    t = e[o]
                }
                return i
            }, e.prototype.cleanUpEvents = function () {
                var e = this;
                e.options.dots && null !== e.$dots && (i("li", e.$dots).off("click.slick", e.changeSlide).off("mouseenter.slick", i.proxy(e.interrupt, e, !0)).off("mouseleave.slick", i.proxy(e.interrupt, e, !1)), !0 === e.options.accessibility && e.$dots.off("keydown.slick", e.keyHandler)), e.$slider.off("focus.slick blur.slick"), !0 === e.options.arrows && e.slideCount > e.options.slidesToShow && (e.$prevArrow && e.$prevArrow.off("click.slick", e.changeSlide), e.$nextArrow && e.$nextArrow.off("click.slick", e.changeSlide), !0 === e.options.accessibility && (e.$prevArrow && e.$prevArrow.off("keydown.slick", e.keyHandler), e.$nextArrow && e.$nextArrow.off("keydown.slick", e.keyHandler))), e.$list.off("touchstart.slick mousedown.slick", e.swipeHandler), e.$list.off("touchmove.slick mousemove.slick", e.swipeHandler), e.$list.off("touchend.slick mouseup.slick", e.swipeHandler), e.$list.off("touchcancel.slick mouseleave.slick", e.swipeHandler), e.$list.off("click.slick", e.clickHandler), i(document).off(e.visibilityChange, e.visibility), e.cleanUpSlideEvents(), !0 === e.options.accessibility && e.$list.off("keydown.slick", e.keyHandler), !0 === e.options.focusOnSelect && i(e.$slideTrack).children().off("click.slick", e.selectHandler), i(window).off("orientationchange.slick.slick-" + e.instanceUid, e.orientationChange), i(window).off("resize.slick.slick-" + e.instanceUid, e.resize), i("[draggable!=true]", e.$slideTrack).off("dragstart", e.preventDefault), i(window).off("load.slick.slick-" + e.instanceUid, e.setPosition)
            }, e.prototype.cleanUpSlideEvents = function () {
                var e = this;
                e.$list.off("mouseenter.slick", i.proxy(e.interrupt, e, !0)), e.$list.off("mouseleave.slick", i.proxy(e.interrupt, e, !1))
            }, e.prototype.cleanUpRows = function () {
                var i, e = this;
                e.options.rows > 1 && ((i = e.$slides.children().children()).removeAttr("style"), e.$slider.empty().append(i))
            }, e.prototype.clickHandler = function (i) {
                !1 === this.shouldClick && (i.stopImmediatePropagation(), i.stopPropagation(), i.preventDefault())
            }, e.prototype.destroy = function (e) {
                var t = this;
                t.autoPlayClear(), t.touchObject = {}, t.cleanUpEvents(), i(".slick-cloned", t.$slider).detach(), t.$dots && t.$dots.remove(), t.$prevArrow && t.$prevArrow.length && (t.$prevArrow.removeClass("slick-disabled slick-arrow slick-hidden").removeAttr("aria-hidden aria-disabled tabindex").css("display", ""), t.htmlExpr.test(t.options.prevArrow) && t.$prevArrow.remove()), t.$nextArrow && t.$nextArrow.length && (t.$nextArrow.removeClass("slick-disabled slick-arrow slick-hidden").removeAttr("aria-hidden aria-disabled tabindex").css("display", ""), t.htmlExpr.test(t.options.nextArrow) && t.$nextArrow.remove()), t.$slides && (t.$slides.removeClass("slick-slide slick-active slick-center slick-visible slick-current").removeAttr("aria-hidden").removeAttr("data-slick-index").each(function () {
                    i(this).attr("style", i(this).data("originalStyling"))
                }), t.$slideTrack.children(this.options.slide).detach(), t.$slideTrack.detach(), t.$list.detach(), t.$slider.append(t.$slides)), t.cleanUpRows(), t.$slider.removeClass("slick-slider"), t.$slider.removeClass("slick-initialized"), t.$slider.removeClass("slick-dotted"), t.unslicked = !0, e || t.$slider.trigger("destroy", [t])
            }, e.prototype.disableTransition = function (i) {
                var e = this, t = {};
                t[e.transitionType] = "", !1 === e.options.fade ? e.$slideTrack.css(t) : e.$slides.eq(i).css(t)
            }, e.prototype.fadeSlide = function (i, e) {
                var t = this;
                !1 === t.cssTransitions ? (t.$slides.eq(i).css({zIndex: t.options.zIndex}), t.$slides.eq(i).animate({opacity: 1}, t.options.speed, t.options.easing, e)) : (t.applyTransition(i), t.$slides.eq(i).css({
                    opacity: 1,
                    zIndex: t.options.zIndex
                }), e && setTimeout(function () {
                    t.disableTransition(i), e.call()
                }, t.options.speed))
            }, e.prototype.fadeSlideOut = function (i) {
                var e = this;
                !1 === e.cssTransitions ? e.$slides.eq(i).animate({
                    opacity: 0,
                    zIndex: e.options.zIndex - 2
                }, e.options.speed, e.options.easing) : (e.applyTransition(i), e.$slides.eq(i).css({
                    opacity: 0,
                    zIndex: e.options.zIndex - 2
                }))
            }, e.prototype.filterSlides = e.prototype.slickFilter = function (i) {
                var e = this;
                null !== i && (e.$slidesCache = e.$slides, e.unload(), e.$slideTrack.children(this.options.slide).detach(), e.$slidesCache.filter(i).appendTo(e.$slideTrack), e.reinit())
            }, e.prototype.focusHandler = function () {
                var e = this;
                e.$slider.off("focus.slick blur.slick").on("focus.slick blur.slick", "*", function (t) {
                    t.stopImmediatePropagation();
                    var o = i(this);
                    setTimeout(function () {
                        e.options.pauseOnFocus && (e.focussed = o.is(":focus"), e.autoPlay())
                    }, 0)
                })
            }, e.prototype.getCurrent = e.prototype.slickCurrentSlide = function () {
                return this.currentSlide
            }, e.prototype.getDotCount = function () {
                var i = this, e = 0, t = 0, o = 0;
                if (!0 === i.options.infinite) if (i.slideCount <= i.options.slidesToShow) ++o; else for (; e < i.slideCount;) ++o, e = t + i.options.slidesToScroll, t += i.options.slidesToScroll <= i.options.slidesToShow ? i.options.slidesToScroll : i.options.slidesToShow; else if (!0 === i.options.centerMode) o = i.slideCount; else if (i.options.asNavFor) for (; e < i.slideCount;) ++o, e = t + i.options.slidesToScroll, t += i.options.slidesToScroll <= i.options.slidesToShow ? i.options.slidesToScroll : i.options.slidesToShow; else o = 1 + Math.ceil((i.slideCount - i.options.slidesToShow) / i.options.slidesToScroll);
                return o - 1
            }, e.prototype.getLeft = function (i) {
                var e, t, o, s, n = this, r = 0;
                return n.slideOffset = 0, t = n.$slides.first().outerHeight(!0), !0 === n.options.infinite ? (n.slideCount > n.options.slidesToShow && (n.slideOffset = n.slideWidth * n.options.slidesToShow * -1, s = -1, !0 === n.options.vertical && !0 === n.options.centerMode && (2 === n.options.slidesToShow ? s = -1.5 : 1 === n.options.slidesToShow && (s = -2)), r = t * n.options.slidesToShow * s), n.slideCount % n.options.slidesToScroll != 0 && i + n.options.slidesToScroll > n.slideCount && n.slideCount > n.options.slidesToShow && (i > n.slideCount ? (n.slideOffset = (n.options.slidesToShow - (i - n.slideCount)) * n.slideWidth * -1, r = (n.options.slidesToShow - (i - n.slideCount)) * t * -1) : (n.slideOffset = n.slideCount % n.options.slidesToScroll * n.slideWidth * -1, r = n.slideCount % n.options.slidesToScroll * t * -1))) : i + n.options.slidesToShow > n.slideCount && (n.slideOffset = (i + n.options.slidesToShow - n.slideCount) * n.slideWidth, r = (i + n.options.slidesToShow - n.slideCount) * t), n.slideCount <= n.options.slidesToShow && (n.slideOffset = 0, r = 0), !0 === n.options.centerMode && n.slideCount <= n.options.slidesToShow ? n.slideOffset = n.slideWidth * Math.floor(n.options.slidesToShow) / 2 - n.slideWidth * n.slideCount / 2 : !0 === n.options.centerMode && !0 === n.options.infinite ? n.slideOffset += n.slideWidth * Math.floor(n.options.slidesToShow / 2) - n.slideWidth : !0 === n.options.centerMode && (n.slideOffset = 0, n.slideOffset += n.slideWidth * Math.floor(n.options.slidesToShow / 2)), e = !1 === n.options.vertical ? i * n.slideWidth * -1 + n.slideOffset : i * t * -1 + r, !0 === n.options.variableWidth && (o = n.slideCount <= n.options.slidesToShow || !1 === n.options.infinite ? n.$slideTrack.children(".slick-slide").eq(i) : n.$slideTrack.children(".slick-slide").eq(i + n.options.slidesToShow), e = !0 === n.options.rtl ? o[0] ? -1 * (n.$slideTrack.width() - o[0].offsetLeft - o.width()) : 0 : o[0] ? -1 * o[0].offsetLeft : 0, !0 === n.options.centerMode && (o = n.slideCount <= n.options.slidesToShow || !1 === n.options.infinite ? n.$slideTrack.children(".slick-slide").eq(i) : n.$slideTrack.children(".slick-slide").eq(i + n.options.slidesToShow + 1), e = !0 === n.options.rtl ? o[0] ? -1 * (n.$slideTrack.width() - o[0].offsetLeft - o.width()) : 0 : o[0] ? -1 * o[0].offsetLeft : 0, e += (n.$list.width() - o.outerWidth()) / 2)), e
            }, e.prototype.getOption = e.prototype.slickGetOption = function (i) {
                return this.options[i]
            }, e.prototype.getNavigableIndexes = function () {
                var i, e = this, t = 0, o = 0, s = [];
                for (!1 === e.options.infinite ? i = e.slideCount : (t = -1 * e.options.slidesToScroll, o = -1 * e.options.slidesToScroll, i = 2 * e.slideCount); t < i;) s.push(t), t = o + e.options.slidesToScroll, o += e.options.slidesToScroll <= e.options.slidesToShow ? e.options.slidesToScroll : e.options.slidesToShow;
                return s
            }, e.prototype.getSlick = function () {
                return this
            }, e.prototype.getSlideCount = function () {
                var e, t, o = this;
                return t = !0 === o.options.centerMode ? o.slideWidth * Math.floor(o.options.slidesToShow / 2) : 0, !0 === o.options.swipeToSlide ? (o.$slideTrack.find(".slick-slide").each(function (s, n) {
                    if (n.offsetLeft - t + i(n).outerWidth() / 2 > -1 * o.swipeLeft) return e = n, !1
                }), Math.abs(i(e).attr("data-slick-index") - o.currentSlide) || 1) : o.options.slidesToScroll
            }, e.prototype.goTo = e.prototype.slickGoTo = function (i, e) {
                this.changeSlide({data: {message: "index", index: parseInt(i)}}, e)
            }, e.prototype.init = function (e) {
                var t = this;
                i(t.$slider).hasClass("slick-initialized") || (i(t.$slider).addClass("slick-initialized"), t.buildRows(), t.buildOut(), t.setProps(), t.startLoad(), t.loadSlider(), t.initializeEvents(), t.updateArrows(), t.updateDots(), t.checkResponsive(!0), t.focusHandler()), e && t.$slider.trigger("init", [t]), !0 === t.options.accessibility && t.initADA(), t.options.autoplay && (t.paused = !1, t.autoPlay())
            }, e.prototype.initADA = function () {
                var e = this, t = Math.ceil(e.slideCount / e.options.slidesToShow),
                    o = e.getNavigableIndexes().filter(function (i) {
                        return i >= 0 && i < e.slideCount
                    });
                e.$slides.add(e.$slideTrack.find(".slick-cloned")).attr({
                    "aria-hidden": "true",
                    tabindex: "-1"
                }).find("a, input, button, select").attr({tabindex: "-1"}), null !== e.$dots && (e.$slides.not(e.$slideTrack.find(".slick-cloned")).each(function (t) {
                    var s = o.indexOf(t);
                    i(this).attr({
                        role: "tabpanel",
                        id: "slick-slide" + e.instanceUid + t,
                        tabindex: -1
                    }), -1 !== s && i(this).attr({"aria-describedby": "slick-slide-control" + e.instanceUid + s})
                }), e.$dots.attr("role", "tablist").find("li").each(function (s) {
                    var n = o[s];
                    i(this).attr({role: "presentation"}), i(this).find("button").first().attr({
                        role: "tab",
                        id: "slick-slide-control" + e.instanceUid + s,
                        "aria-controls": "slick-slide" + e.instanceUid + n,
                        "aria-label": s + 1 + " of " + t,
                        "aria-selected": null,
                        tabindex: "-1"
                    })
                }).eq(e.currentSlide).find("button").attr({"aria-selected": "true", tabindex: "0"}).end());
                for (var s = e.currentSlide, n = s + e.options.slidesToShow; s < n; s++) e.$slides.eq(s).attr("tabindex", 0);
                e.activateADA()
            }, e.prototype.initArrowEvents = function () {
                var i = this;
                !0 === i.options.arrows && i.slideCount > i.options.slidesToShow && (i.$prevArrow.off("click.slick").on("click.slick", {message: "previous"}, i.changeSlide), i.$nextArrow.off("click.slick").on("click.slick", {message: "next"}, i.changeSlide), !0 === i.options.accessibility && (i.$prevArrow.on("keydown.slick", i.keyHandler), i.$nextArrow.on("keydown.slick", i.keyHandler)))
            }, e.prototype.initDotEvents = function () {
                var e = this;
                !0 === e.options.dots && (i("li", e.$dots).on("click.slick", {message: "index"}, e.changeSlide), !0 === e.options.accessibility && e.$dots.on("keydown.slick", e.keyHandler)), !0 === e.options.dots && !0 === e.options.pauseOnDotsHover && i("li", e.$dots).on("mouseenter.slick", i.proxy(e.interrupt, e, !0)).on("mouseleave.slick", i.proxy(e.interrupt, e, !1))
            }, e.prototype.initSlideEvents = function () {
                var e = this;
                e.options.pauseOnHover && (e.$list.on("mouseenter.slick", i.proxy(e.interrupt, e, !0)), e.$list.on("mouseleave.slick", i.proxy(e.interrupt, e, !1)))
            }, e.prototype.initializeEvents = function () {
                var e = this;
                e.initArrowEvents(), e.initDotEvents(), e.initSlideEvents(), e.$list.on("touchstart.slick mousedown.slick", {action: "start"}, e.swipeHandler), e.$list.on("touchmove.slick mousemove.slick", {action: "move"}, e.swipeHandler), e.$list.on("touchend.slick mouseup.slick", {action: "end"}, e.swipeHandler), e.$list.on("touchcancel.slick mouseleave.slick", {action: "end"}, e.swipeHandler), e.$list.on("click.slick", e.clickHandler), i(document).on(e.visibilityChange, i.proxy(e.visibility, e)), !0 === e.options.accessibility && e.$list.on("keydown.slick", e.keyHandler), !0 === e.options.focusOnSelect && i(e.$slideTrack).children().on("click.slick", e.selectHandler), i(window).on("orientationchange.slick.slick-" + e.instanceUid, i.proxy(e.orientationChange, e)), i(window).on("resize.slick.slick-" + e.instanceUid, i.proxy(e.resize, e)), i("[draggable!=true]", e.$slideTrack).on("dragstart", e.preventDefault), i(window).on("load.slick.slick-" + e.instanceUid, e.setPosition), i(e.setPosition)
            }, e.prototype.initUI = function () {
                var i = this;
                !0 === i.options.arrows && i.slideCount > i.options.slidesToShow && (i.$prevArrow.show(), i.$nextArrow.show()), !0 === i.options.dots && i.slideCount > i.options.slidesToShow && i.$dots.show()
            }, e.prototype.keyHandler = function (i) {
                var e = this;
                i.target.tagName.match("TEXTAREA|INPUT|SELECT") || (37 === i.keyCode && !0 === e.options.accessibility ? e.changeSlide({data: {message: !0 === e.options.rtl ? "next" : "previous"}}) : 39 === i.keyCode && !0 === e.options.accessibility && e.changeSlide({data: {message: !0 === e.options.rtl ? "previous" : "next"}}))
            }, e.prototype.lazyLoad = function () {
                function e(e) {
                    i("img[data-lazy]", e).each(function () {
                        var e = i(this), t = i(this).attr("data-lazy"), o = i(this).attr("data-srcset"),
                            s = i(this).attr("data-sizes") || n.$slider.attr("data-sizes"),
                            r = document.createElement("img");
                        r.onload = function () {
                            e.animate({opacity: 0}, 100, function () {
                                o && (e.attr("srcset", o), s && e.attr("sizes", s)), e.attr("src", t).animate({opacity: 1}, 200, function () {
                                    e.removeAttr("data-lazy data-srcset data-sizes").removeClass("slick-loading")
                                }), n.$slider.trigger("lazyLoaded", [n, e, t])
                            })
                        }, r.onerror = function () {
                            e.removeAttr("data-lazy").removeClass("slick-loading").addClass("slick-lazyload-error"), n.$slider.trigger("lazyLoadError", [n, e, t])
                        }, r.src = t
                    })
                }

                var t, o, s, n = this;
                if (!0 === n.options.centerMode ? !0 === n.options.infinite ? s = (o = n.currentSlide + (n.options.slidesToShow / 2 + 1)) + n.options.slidesToShow + 2 : (o = Math.max(0, n.currentSlide - (n.options.slidesToShow / 2 + 1)), s = n.options.slidesToShow / 2 + 1 + 2 + n.currentSlide) : (o = n.options.infinite ? n.options.slidesToShow + n.currentSlide : n.currentSlide, s = Math.ceil(o + n.options.slidesToShow), !0 === n.options.fade && (o > 0 && o--, s <= n.slideCount && s++)), t = n.$slider.find(".slick-slide").slice(o, s), "anticipated" === n.options.lazyLoad) for (var r = o - 1, l = s, d = n.$slider.find(".slick-slide"), a = 0; a < n.options.slidesToScroll; a++) r < 0 && (r = n.slideCount - 1), t = (t = t.add(d.eq(r))).add(d.eq(l)), r--, l++;
                e(t), n.slideCount <= n.options.slidesToShow ? e(n.$slider.find(".slick-slide")) : n.currentSlide >= n.slideCount - n.options.slidesToShow ? e(n.$slider.find(".slick-cloned").slice(0, n.options.slidesToShow)) : 0 === n.currentSlide && e(n.$slider.find(".slick-cloned").slice(-1 * n.options.slidesToShow))
            }, e.prototype.loadSlider = function () {
                var i = this;
                i.setPosition(), i.$slideTrack.css({opacity: 1}), i.$slider.removeClass("slick-loading"), i.initUI(), "progressive" === i.options.lazyLoad && i.progressiveLazyLoad()
            }, e.prototype.next = e.prototype.slickNext = function () {
                this.changeSlide({data: {message: "next"}})
            }, e.prototype.orientationChange = function () {
                var i = this;
                i.checkResponsive(), i.setPosition()
            }, e.prototype.pause = e.prototype.slickPause = function () {
                var i = this;
                i.autoPlayClear(), i.paused = !0
            }, e.prototype.play = e.prototype.slickPlay = function () {
                var i = this;
                i.autoPlay(), i.options.autoplay = !0, i.paused = !1, i.focussed = !1, i.interrupted = !1
            }, e.prototype.postSlide = function (e) {
                var t = this;
                t.unslicked || (t.$slider.trigger("afterChange", [t, e]), t.animating = !1, t.slideCount > t.options.slidesToShow && t.setPosition(), t.swipeLeft = null, t.options.autoplay && t.autoPlay(), !0 === t.options.accessibility && (t.initADA(), t.options.focusOnChange && i(t.$slides.get(t.currentSlide)).attr("tabindex", 0).focus()))
            }, e.prototype.prev = e.prototype.slickPrev = function () {
                this.changeSlide({data: {message: "previous"}})
            }, e.prototype.preventDefault = function (i) {
                i.preventDefault()
            }, e.prototype.progressiveLazyLoad = function (e) {
                e = e || 1;
                var t, o, s, n, r, l = this, d = i("img[data-lazy]", l.$slider);
                d.length ? (t = d.first(), o = t.attr("data-lazy"), s = t.attr("data-srcset"), n = t.attr("data-sizes") || l.$slider.attr("data-sizes"), (r = document.createElement("img")).onload = function () {
                    s && (t.attr("srcset", s), n && t.attr("sizes", n)), t.attr("src", o).removeAttr("data-lazy data-srcset data-sizes").removeClass("slick-loading"), !0 === l.options.adaptiveHeight && l.setPosition(), l.$slider.trigger("lazyLoaded", [l, t, o]), l.progressiveLazyLoad()
                }, r.onerror = function () {
                    e < 3 ? setTimeout(function () {
                        l.progressiveLazyLoad(e + 1)
                    }, 500) : (t.removeAttr("data-lazy").removeClass("slick-loading").addClass("slick-lazyload-error"), l.$slider.trigger("lazyLoadError", [l, t, o]), l.progressiveLazyLoad())
                }, r.src = o) : l.$slider.trigger("allImagesLoaded", [l])
            }, e.prototype.refresh = function (e) {
                var t, o, s = this;
                o = s.slideCount - s.options.slidesToShow, !s.options.infinite && s.currentSlide > o && (s.currentSlide = o), s.slideCount <= s.options.slidesToShow && (s.currentSlide = 0), t = s.currentSlide, s.destroy(!0), i.extend(s, s.initials, {currentSlide: t}), s.init(), e || s.changeSlide({
                    data: {
                        message: "index",
                        index: t
                    }
                }, !1)
            }, e.prototype.registerBreakpoints = function () {
                var e, t, o, s = this, n = s.options.responsive || null;
                if ("array" === i.type(n) && n.length) {
                    s.respondTo = s.options.respondTo || "window";
                    for (e in n) if (o = s.breakpoints.length - 1, n.hasOwnProperty(e)) {
                        for (t = n[e].breakpoint; o >= 0;) s.breakpoints[o] && s.breakpoints[o] === t && s.breakpoints.splice(o, 1), o--;
                        s.breakpoints.push(t), s.breakpointSettings[t] = n[e].settings
                    }
                    s.breakpoints.sort(function (i, e) {
                        return s.options.mobileFirst ? i - e : e - i
                    })
                }
            }, e.prototype.reinit = function () {
                var e = this;
                e.$slides = e.$slideTrack.children(e.options.slide).addClass("slick-slide"), e.slideCount = e.$slides.length, e.currentSlide >= e.slideCount && 0 !== e.currentSlide && (e.currentSlide = e.currentSlide - e.options.slidesToScroll), e.slideCount <= e.options.slidesToShow && (e.currentSlide = 0), e.registerBreakpoints(), e.setProps(), e.setupInfinite(), e.buildArrows(), e.updateArrows(), e.initArrowEvents(), e.buildDots(), e.updateDots(), e.initDotEvents(), e.cleanUpSlideEvents(), e.initSlideEvents(), e.checkResponsive(!1, !0), !0 === e.options.focusOnSelect && i(e.$slideTrack).children().on("click.slick", e.selectHandler), e.setSlideClasses("number" == typeof e.currentSlide ? e.currentSlide : 0), e.setPosition(), e.focusHandler(), e.paused = !e.options.autoplay, e.autoPlay(), e.$slider.trigger("reInit", [e])
            }, e.prototype.resize = function () {
                var e = this;
                i(window).width() !== e.windowWidth && (clearTimeout(e.windowDelay), e.windowDelay = window.setTimeout(function () {
                    e.windowWidth = i(window).width(), e.checkResponsive(), e.unslicked || e.setPosition()
                }, 50))
            }, e.prototype.removeSlide = e.prototype.slickRemove = function (i, e, t) {
                var o = this;
                if (i = "boolean" == typeof i ? !0 === (e = i) ? 0 : o.slideCount - 1 : !0 === e ? --i : i, o.slideCount < 1 || i < 0 || i > o.slideCount - 1) return !1;
                o.unload(), !0 === t ? o.$slideTrack.children().remove() : o.$slideTrack.children(this.options.slide).eq(i).remove(), o.$slides = o.$slideTrack.children(this.options.slide), o.$slideTrack.children(this.options.slide).detach(), o.$slideTrack.append(o.$slides), o.$slidesCache = o.$slides, o.reinit()
            }, e.prototype.setCSS = function (i) {
                var e, t, o = this, s = {};
                !0 === o.options.rtl && (i = -i), e = "left" == o.positionProp ? Math.ceil(i) + "px" : "0px", t = "top" == o.positionProp ? Math.ceil(i) + "px" : "0px", s[o.positionProp] = i, !1 === o.transformsEnabled ? o.$slideTrack.css(s) : (s = {}, !1 === o.cssTransitions ? (s[o.animType] = "translate(" + e + ", " + t + ")", o.$slideTrack.css(s)) : (s[o.animType] = "translate3d(" + e + ", " + t + ", 0px)", o.$slideTrack.css(s)))
            }, e.prototype.setDimensions = function () {
                var i = this;
                !1 === i.options.vertical ? !0 === i.options.centerMode && i.$list.css({padding: "0px " + i.options.centerPadding}) : (i.$list.height(i.$slides.first().outerHeight(!0) * i.options.slidesToShow), !0 === i.options.centerMode && i.$list.css({padding: i.options.centerPadding + " 0px"})), i.listWidth = i.$list.width(), i.listHeight = i.$list.height(), !1 === i.options.vertical && !1 === i.options.variableWidth ? (i.slideWidth = Math.ceil(i.listWidth / i.options.slidesToShow), i.$slideTrack.width(Math.ceil(i.slideWidth * i.$slideTrack.children(".slick-slide").length))) : !0 === i.options.variableWidth ? i.$slideTrack.width(5e3 * i.slideCount) : (i.slideWidth = Math.ceil(i.listWidth), i.$slideTrack.height(Math.ceil(i.$slides.first().outerHeight(!0) * i.$slideTrack.children(".slick-slide").length)));
                var e = i.$slides.first().outerWidth(!0) - i.$slides.first().width();
                !1 === i.options.variableWidth && i.$slideTrack.children(".slick-slide").width(i.slideWidth - e)
            }, e.prototype.setFade = function () {
                var e, t = this;
                t.$slides.each(function (o, s) {
                    e = t.slideWidth * o * -1, !0 === t.options.rtl ? i(s).css({
                        position: "relative",
                        right: e,
                        top: 0,
                        zIndex: t.options.zIndex - 2,
                        opacity: 0
                    }) : i(s).css({position: "relative", left: e, top: 0, zIndex: t.options.zIndex - 2, opacity: 0})
                }), t.$slides.eq(t.currentSlide).css({zIndex: t.options.zIndex - 1, opacity: 1})
            }, e.prototype.setHeight = function () {
                var i = this;
                if (1 === i.options.slidesToShow && !0 === i.options.adaptiveHeight && !1 === i.options.vertical) {
                    var e = i.$slides.eq(i.currentSlide).outerHeight(!0);
                    i.$list.css("height", e)
                }
            }, e.prototype.setOption = e.prototype.slickSetOption = function () {
                var e, t, o, s, n, r = this, l = !1;
                if ("object" === i.type(arguments[0]) ? (o = arguments[0], l = arguments[1], n = "multiple") : "string" === i.type(arguments[0]) && (o = arguments[0], s = arguments[1], l = arguments[2], "responsive" === arguments[0] && "array" === i.type(arguments[1]) ? n = "responsive" : void 0 !== arguments[1] && (n = "single")), "single" === n) r.options[o] = s; else if ("multiple" === n) i.each(o, function (i, e) {
                    r.options[i] = e
                }); else if ("responsive" === n) for (t in s) if ("array" !== i.type(r.options.responsive)) r.options.responsive = [s[t]]; else {
                    for (e = r.options.responsive.length - 1; e >= 0;) r.options.responsive[e].breakpoint === s[t].breakpoint && r.options.responsive.splice(e, 1), e--;
                    r.options.responsive.push(s[t])
                }
                l && (r.unload(), r.reinit())
            }, e.prototype.setPosition = function () {
                var i = this;
                i.setDimensions(), i.setHeight(), !1 === i.options.fade ? i.setCSS(i.getLeft(i.currentSlide)) : i.setFade(), i.$slider.trigger("setPosition", [i])
            }, e.prototype.setProps = function () {
                var i = this, e = document.body.style;
                i.positionProp = !0 === i.options.vertical ? "top" : "left", "top" === i.positionProp ? i.$slider.addClass("slick-vertical") : i.$slider.removeClass("slick-vertical"), void 0 === e.WebkitTransition && void 0 === e.MozTransition && void 0 === e.msTransition || !0 === i.options.useCSS && (i.cssTransitions = !0), i.options.fade && ("number" == typeof i.options.zIndex ? i.options.zIndex < 3 && (i.options.zIndex = 3) : i.options.zIndex = i.defaults.zIndex), void 0 !== e.OTransform && (i.animType = "OTransform", i.transformType = "-o-transform", i.transitionType = "OTransition", void 0 === e.perspectiveProperty && void 0 === e.webkitPerspective && (i.animType = !1)), void 0 !== e.MozTransform && (i.animType = "MozTransform", i.transformType = "-moz-transform", i.transitionType = "MozTransition", void 0 === e.perspectiveProperty && void 0 === e.MozPerspective && (i.animType = !1)), void 0 !== e.webkitTransform && (i.animType = "webkitTransform", i.transformType = "-webkit-transform", i.transitionType = "webkitTransition", void 0 === e.perspectiveProperty && void 0 === e.webkitPerspective && (i.animType = !1)), void 0 !== e.msTransform && (i.animType = "msTransform", i.transformType = "-ms-transform", i.transitionType = "msTransition", void 0 === e.msTransform && (i.animType = !1)), void 0 !== e.transform && !1 !== i.animType && (i.animType = "transform", i.transformType = "transform", i.transitionType = "transition"), i.transformsEnabled = i.options.useTransform && null !== i.animType && !1 !== i.animType
            }, e.prototype.setSlideClasses = function (i) {
                var e, t, o, s, n = this;
                if (t = n.$slider.find(".slick-slide").removeClass("slick-active slick-center slick-current").attr("aria-hidden", "true"), n.$slides.eq(i).addClass("slick-current"), !0 === n.options.centerMode) {
                    var r = n.options.slidesToShow % 2 == 0 ? 1 : 0;
                    e = Math.floor(n.options.slidesToShow / 2), !0 === n.options.infinite && (i >= e && i <= n.slideCount - 1 - e ? n.$slides.slice(i - e + r, i + e + 1).addClass("slick-active").attr("aria-hidden", "false") : (o = n.options.slidesToShow + i, t.slice(o - e + 1 + r, o + e + 2).addClass("slick-active").attr("aria-hidden", "false")), 0 === i ? t.eq(t.length - 1 - n.options.slidesToShow).addClass("slick-center") : i === n.slideCount - 1 && t.eq(n.options.slidesToShow).addClass("slick-center")), n.$slides.eq(i).addClass("slick-center")
                } else i >= 0 && i <= n.slideCount - n.options.slidesToShow ? n.$slides.slice(i, i + n.options.slidesToShow).addClass("slick-active").attr("aria-hidden", "false") : t.length <= n.options.slidesToShow ? t.addClass("slick-active").attr("aria-hidden", "false") : (s = n.slideCount % n.options.slidesToShow, o = !0 === n.options.infinite ? n.options.slidesToShow + i : i, n.options.slidesToShow == n.options.slidesToScroll && n.slideCount - i < n.options.slidesToShow ? t.slice(o - (n.options.slidesToShow - s), o + s).addClass("slick-active").attr("aria-hidden", "false") : t.slice(o, o + n.options.slidesToShow).addClass("slick-active").attr("aria-hidden", "false"));
                "ondemand" !== n.options.lazyLoad && "anticipated" !== n.options.lazyLoad || n.lazyLoad()
            }, e.prototype.setupInfinite = function () {
                var e, t, o, s = this;
                if (!0 === s.options.fade && (s.options.centerMode = !1), !0 === s.options.infinite && !1 === s.options.fade && (t = null, s.slideCount > s.options.slidesToShow)) {
                    for (o = !0 === s.options.centerMode ? s.options.slidesToShow + 1 : s.options.slidesToShow, e = s.slideCount; e > s.slideCount - o; e -= 1) t = e - 1, i(s.$slides[t]).clone(!0).attr("id", "").attr("data-slick-index", t - s.slideCount).prependTo(s.$slideTrack).addClass("slick-cloned");
                    for (e = 0; e < o + s.slideCount; e += 1) t = e, i(s.$slides[t]).clone(!0).attr("id", "").attr("data-slick-index", t + s.slideCount).appendTo(s.$slideTrack).addClass("slick-cloned");
                    s.$slideTrack.find(".slick-cloned").find("[id]").each(function () {
                        i(this).attr("id", "")
                    })
                }
            }, e.prototype.interrupt = function (i) {
                var e = this;
                i || e.autoPlay(), e.interrupted = i
            }, e.prototype.selectHandler = function (e) {
                var t = this, o = i(e.target).is(".slick-slide") ? i(e.target) : i(e.target).parents(".slick-slide"),
                    s = parseInt(o.attr("data-slick-index"));
                s || (s = 0), t.slideCount <= t.options.slidesToShow ? t.slideHandler(s, !1, !0) : t.slideHandler(s)
            }, e.prototype.slideHandler = function (i, e, t) {
                var o, s, n, r, l, d = null, a = this;
                if (e = e || !1, !(!0 === a.animating && !0 === a.options.waitForAnimate || !0 === a.options.fade && a.currentSlide === i)) if (!1 === e && a.asNavFor(i), o = i, d = a.getLeft(o), r = a.getLeft(a.currentSlide), a.currentLeft = null === a.swipeLeft ? r : a.swipeLeft, !1 === a.options.infinite && !1 === a.options.centerMode && (i < 0 || i > a.getDotCount() * a.options.slidesToScroll)) !1 === a.options.fade && (o = a.currentSlide, !0 !== t ? a.animateSlide(r, function () {
                    a.postSlide(o)
                }) : a.postSlide(o)); else if (!1 === a.options.infinite && !0 === a.options.centerMode && (i < 0 || i > a.slideCount - a.options.slidesToScroll)) !1 === a.options.fade && (o = a.currentSlide, !0 !== t ? a.animateSlide(r, function () {
                    a.postSlide(o)
                }) : a.postSlide(o)); else {
                    if (a.options.autoplay && clearInterval(a.autoPlayTimer), s = o < 0 ? a.slideCount % a.options.slidesToScroll != 0 ? a.slideCount - a.slideCount % a.options.slidesToScroll : a.slideCount + o : o >= a.slideCount ? a.slideCount % a.options.slidesToScroll != 0 ? 0 : o - a.slideCount : o, a.animating = !0, a.$slider.trigger("beforeChange", [a, a.currentSlide, s]), n = a.currentSlide, a.currentSlide = s, a.setSlideClasses(a.currentSlide), a.options.asNavFor && (l = (l = a.getNavTarget()).slick("getSlick")).slideCount <= l.options.slidesToShow && l.setSlideClasses(a.currentSlide), a.updateDots(), a.updateArrows(), !0 === a.options.fade) return !0 !== t ? (a.fadeSlideOut(n), a.fadeSlide(s, function () {
                        a.postSlide(s)
                    })) : a.postSlide(s), void a.animateHeight();
                    !0 !== t ? a.animateSlide(d, function () {
                        a.postSlide(s)
                    }) : a.postSlide(s)
                }
            }, e.prototype.startLoad = function () {
                var i = this;
                !0 === i.options.arrows && i.slideCount > i.options.slidesToShow && (i.$prevArrow.hide(), i.$nextArrow.hide()), !0 === i.options.dots && i.slideCount > i.options.slidesToShow && i.$dots.hide(), i.$slider.addClass("slick-loading")
            }, e.prototype.swipeDirection = function () {
                var i, e, t, o, s = this;
                return i = s.touchObject.startX - s.touchObject.curX, e = s.touchObject.startY - s.touchObject.curY, t = Math.atan2(e, i), (o = Math.round(180 * t / Math.PI)) < 0 && (o = 360 - Math.abs(o)), o <= 45 && o >= 0 ? !1 === s.options.rtl ? "left" : "right" : o <= 360 && o >= 315 ? !1 === s.options.rtl ? "left" : "right" : o >= 135 && o <= 225 ? !1 === s.options.rtl ? "right" : "left" : !0 === s.options.verticalSwiping ? o >= 35 && o <= 135 ? "down" : "up" : "vertical"
            }, e.prototype.swipeEnd = function (i) {
                var e, t, o = this;
                if (o.dragging = !1, o.swiping = !1, o.scrolling) return o.scrolling = !1, !1;
                if (o.interrupted = !1, o.shouldClick = !(o.touchObject.swipeLength > 10), void 0 === o.touchObject.curX) return !1;
                if (!0 === o.touchObject.edgeHit && o.$slider.trigger("edge", [o, o.swipeDirection()]), o.touchObject.swipeLength >= o.touchObject.minSwipe) {
                    switch (t = o.swipeDirection()) {
                        case"left":
                        case"down":
                            e = o.options.swipeToSlide ? o.checkNavigable(o.currentSlide + o.getSlideCount()) : o.currentSlide + o.getSlideCount(), o.currentDirection = 0;
                            break;
                        case"right":
                        case"up":
                            e = o.options.swipeToSlide ? o.checkNavigable(o.currentSlide - o.getSlideCount()) : o.currentSlide - o.getSlideCount(), o.currentDirection = 1
                    }
                    "vertical" != t && (o.slideHandler(e), o.touchObject = {}, o.$slider.trigger("swipe", [o, t]))
                } else o.touchObject.startX !== o.touchObject.curX && (o.slideHandler(o.currentSlide), o.touchObject = {})
            }, e.prototype.swipeHandler = function (i) {
                var e = this;
                if (!(!1 === e.options.swipe || "ontouchend" in document && !1 === e.options.swipe || !1 === e.options.draggable && -1 !== i.type.indexOf("mouse"))) switch (e.touchObject.fingerCount = i.originalEvent && void 0 !== i.originalEvent.touches ? i.originalEvent.touches.length : 1, e.touchObject.minSwipe = e.listWidth / e.options.touchThreshold, !0 === e.options.verticalSwiping && (e.touchObject.minSwipe = e.listHeight / e.options.touchThreshold), i.data.action) {
                    case"start":
                        e.swipeStart(i);
                        break;
                    case"move":
                        e.swipeMove(i);
                        break;
                    case"end":
                        e.swipeEnd(i)
                }
            }, e.prototype.swipeMove = function (i) {
                var e, t, o, s, n, r, l = this;
                return n = void 0 !== i.originalEvent ? i.originalEvent.touches : null, !(!l.dragging || l.scrolling || n && 1 !== n.length) && (e = l.getLeft(l.currentSlide), l.touchObject.curX = void 0 !== n ? n[0].pageX : i.clientX, l.touchObject.curY = void 0 !== n ? n[0].pageY : i.clientY, l.touchObject.swipeLength = Math.round(Math.sqrt(Math.pow(l.touchObject.curX - l.touchObject.startX, 2))), r = Math.round(Math.sqrt(Math.pow(l.touchObject.curY - l.touchObject.startY, 2))), !l.options.verticalSwiping && !l.swiping && r > 4 ? (l.scrolling = !0, !1) : (!0 === l.options.verticalSwiping && (l.touchObject.swipeLength = r), t = l.swipeDirection(), void 0 !== i.originalEvent && l.touchObject.swipeLength > 4 && (l.swiping = !0, i.preventDefault()), s = (!1 === l.options.rtl ? 1 : -1) * (l.touchObject.curX > l.touchObject.startX ? 1 : -1), !0 === l.options.verticalSwiping && (s = l.touchObject.curY > l.touchObject.startY ? 1 : -1), o = l.touchObject.swipeLength, l.touchObject.edgeHit = !1, !1 === l.options.infinite && (0 === l.currentSlide && "right" === t || l.currentSlide >= l.getDotCount() && "left" === t) && (o = l.touchObject.swipeLength * l.options.edgeFriction, l.touchObject.edgeHit = !0), !1 === l.options.vertical ? l.swipeLeft = e + o * s : l.swipeLeft = e + o * (l.$list.height() / l.listWidth) * s, !0 === l.options.verticalSwiping && (l.swipeLeft = e + o * s), !0 !== l.options.fade && !1 !== l.options.touchMove && (!0 === l.animating ? (l.swipeLeft = null, !1) : void l.setCSS(l.swipeLeft))))
            }, e.prototype.swipeStart = function (i) {
                var e, t = this;
                if (t.interrupted = !0, 1 !== t.touchObject.fingerCount || t.slideCount <= t.options.slidesToShow) return t.touchObject = {}, !1;
                void 0 !== i.originalEvent && void 0 !== i.originalEvent.touches && (e = i.originalEvent.touches[0]), t.touchObject.startX = t.touchObject.curX = void 0 !== e ? e.pageX : i.clientX, t.touchObject.startY = t.touchObject.curY = void 0 !== e ? e.pageY : i.clientY, t.dragging = !0
            }, e.prototype.unfilterSlides = e.prototype.slickUnfilter = function () {
                var i = this;
                null !== i.$slidesCache && (i.unload(), i.$slideTrack.children(this.options.slide).detach(), i.$slidesCache.appendTo(i.$slideTrack), i.reinit())
            }, e.prototype.unload = function () {
                var e = this;
                i(".slick-cloned", e.$slider).remove(), e.$dots && e.$dots.remove(), e.$prevArrow && e.htmlExpr.test(e.options.prevArrow) && e.$prevArrow.remove(), e.$nextArrow && e.htmlExpr.test(e.options.nextArrow) && e.$nextArrow.remove(), e.$slides.removeClass("slick-slide slick-active slick-visible slick-current").attr("aria-hidden", "true").css("width", "")
            }, e.prototype.unslick = function (i) {
                var e = this;
                e.$slider.trigger("unslick", [e, i]), e.destroy()
            }, e.prototype.updateArrows = function () {
                var i = this;
                Math.floor(i.options.slidesToShow / 2), !0 === i.options.arrows && i.slideCount > i.options.slidesToShow && !i.options.infinite && (i.$prevArrow.removeClass("slick-disabled").attr("aria-disabled", "false"), i.$nextArrow.removeClass("slick-disabled").attr("aria-disabled", "false"), 0 === i.currentSlide ? (i.$prevArrow.addClass("slick-disabled").attr("aria-disabled", "true"), i.$nextArrow.removeClass("slick-disabled").attr("aria-disabled", "false")) : i.currentSlide >= i.slideCount - i.options.slidesToShow && !1 === i.options.centerMode ? (i.$nextArrow.addClass("slick-disabled").attr("aria-disabled", "true"), i.$prevArrow.removeClass("slick-disabled").attr("aria-disabled", "false")) : i.currentSlide >= i.slideCount - 1 && !0 === i.options.centerMode && (i.$nextArrow.addClass("slick-disabled").attr("aria-disabled", "true"), i.$prevArrow.removeClass("slick-disabled").attr("aria-disabled", "false")))
            }, e.prototype.updateDots = function () {
                var i = this;
                null !== i.$dots && (i.$dots.find("li").removeClass("slick-active").end(), i.$dots.find("li").eq(Math.floor(i.currentSlide / i.options.slidesToScroll)).addClass("slick-active"))
            }, e.prototype.visibility = function () {
                var i = this;
                i.options.autoplay && (document[i.hidden] ? i.interrupted = !0 : i.interrupted = !1)
            }, i.fn.slick = function () {
                var i, t, o = this, s = arguments[0], n = Array.prototype.slice.call(arguments, 1), r = o.length;
                for (i = 0; i < r; i++) if ("object" == typeof s || void 0 === s ? o[i].slick = new e(o[i], s) : t = o[i].slick[s].apply(o[i].slick, n), void 0 !== t) return t;
                return o
            }
        });
        var Slider = function () {
            let $block = $('.Slider').not('.Slider_carousel'), $container = $block.children('.Slider-box'),
                $carousel = $('.Slider_carousel'), $containerCar = $carousel.children('.Slider-box');
            return {
                init: function () {
                    function getData(address, data, cb) {
                        $.ajax({
                            url: address,
                            type: 'GET',
                            dataType: 'json',
                            async: true,
                            data: data,
                            complete: function (result) {
                                if (result.status === 200) {
                                    cb(result.responseJSON);
                                } else {
                                    alert('Ошибка ' + result.status);
                                }
                            }
                        });
                    }

                    $container.each(function () {
                        var $this = $(this);
                        var $navigate = $this.closest($block).find('.Slider-navigate');
                        $this.slick({
                            dots: true,
                            arrows: true,
                            autoplay: true,
                            appendArrows: $navigate,
                            appendDots: $navigate,
                            autoplaySpeed: 3000
                        });
                    });
                    $containerCar.each(function () {
                        var $this = $(this);
                        var $navigate = $this.closest($carousel).find('.Slider-navigate');
                        if ($this.hasClass('Cards')) {
                            $this.slick({
                                appendArrows: $navigate,
                                appendDots: $navigate,
                                dots: false,
                                arrows: true,
                                slidesToShow: 1,
                                infinite: false,
                                slidesToScroll: 1,
                                mobileFirst: true,
                                responsive: [{
                                    breakpoint: 990,
                                    settings: {slidesToShow: 5, slidesToScroll: 1}
                                }, {breakpoint: 480, settings: {slidesToShow: 3, slidesToScroll: 1}}]
                            });
                            $this.on('afterChange', function (e, slick) {
                                if ($(slick.$slides[slick.$slides.length - 3]).hasClass('slick-active')) {
                                    if ($(slick.$slider).data('load')) {
                                        $(slick.$slider).closest('.Slider').addClass('Slider_load');

                                        function sendData(address) {
                                            getData(address, {
                                                offset: function () {
                                                    var oldOffset = $(slick.$slider).data('loadoffset');
                                                    $(slick.$slider).data('loadoffset', ++oldOffset);
                                                    return $(slick.$slider).data('loadoffset');
                                                },
                                                limit: $(slick.$slider).data('loadlimit')
                                            }, function (result) {
                                                result.books.forEach(function (v, i) {
                                                    $this.slick('slickAdd', '<div class="Slider-item"><div class="Slider-content">' + Card().bookTemplate(v) + '</div></div>');
                                                    if (i === (result.books.length - 1)) {
                                                        $(slick.$slider).closest('.Slider').removeClass('Slider_load');
                                                    }
                                                });
                                                setTimeout(function () {
                                                    $(slick.$slider).closest('.Slider').find('.slick-next').trigger('click');
                                                }, 500);
                                            });
                                        };
                                        switch ($(slick.$slider).data('load')) {
                                            case'recommended':
                                                sendData('/api/books/recommended/page')
                                                break;
                                            case'recent':
                                                sendData('/api/recent/page')
                                                break;
                                            case'popular':
                                                sendData('/api/popular/page')
                                                break;
                                            case'recently':
                                                sendData('/api/books/recently/page')
                                                break;
                                        }
                                    }
                                }
                            });
                        } else {
                            $this.slick({
                                appendArrows: $navigate,
                                appendDots: $navigate,
                                dots: true,
                                arrows: true,
                                slidesToShow: 4,
                                slidesToScroll: 2,
                                responsive: [{
                                    breakpoint: 1600,
                                    settings: {slidesToShow: 3, slidesToScroll: 2}
                                }, {breakpoint: 1230, settings: {slidesToShow: 2, slidesToScroll: 2}}, {
                                    breakpoint: 570,
                                    settings: {slidesToShow: 1, slidesToScroll: 1}
                                }]
                            });
                        }
                    });
                }
            };
        };
        Slider().init();
        var Spoiler = function () {
            var $HideBlock = $('.Spoiler');
            var $trigger = $HideBlock.find('.Spoiler-trigger');
            $HideBlock.addClass('Spoiler_CLOSE');
            return {
                init: function () {
                    $trigger.on('click', function (e) {
                        e.preventDefault();
                        var $this = $(this);
                        var scroll = $(window).scrollTop();
                        var $parent = $this.closest($HideBlock);
                        if ($parent.hasClass('Spoiler_CLOSE')) {
                            $parent.removeClass('Spoiler_CLOSE');
                            $(window).scrollTop(scroll);
                        } else {
                            $parent.addClass('Spoiler_CLOSE');
                            $(window).scrollTop(scroll);
                        }
                    });
                }
            };
        };
        Spoiler().init();
        var Tabs = function () {
            var $tabs = $('.Tabs');
            var $tabsLink = $('.Tabs-link');
            var $tabsBlock = $('.Tabs-block');
            return {
                init: function () {
                    $tabsLink.on('click', function (e) {
                        var $this = $(this);
                        var href = $this.attr('href');
                        if (href[0] === "#") {
                            e.preventDefault();
                            var $parent = $this.closest($tabs);
                            if ($parent.hasClass('Tabs_steps')) {
                            } else {
                                var $blocks = $parent.find($tabsBlock).not($parent.find($tabs).find($tabsBlock));
                                var $links = $this.add($this.siblings($tabsLink));
                                var $active = $(href);
                                $links.removeClass('Tabs-link_ACTIVE');
                                $this.addClass('Tabs-link_ACTIVE');
                                $blocks.hide(0);
                                $active.show(0);
                            }
                        }
                    });
                    $('.TabsLink').on('click', function (e) {
                        var $this = $(this);
                        var href = $this.attr('href');
                        var $active = $(href);
                        var $parent = $active.closest($tabs);
                        if ($parent.hasClass('Tabs_steps')) {
                        } else {
                            var $blocks = $parent.find($tabsBlock).not($parent.find($tabs).find($tabsBlock));
                            var $link = $('.Tabs-link[href="' + href + '"]');
                            var $links = $link.add($link.siblings($tabsLink));
                            $links.removeClass('Tabs-link_ACTIVE');
                            $link.addClass('Tabs-link_ACTIVE');
                            $blocks.hide(0);
                            $active.show(0);
                        }
                    });
                    $tabs.each(function () {
                        $(this).find($tabsLink).eq(0).trigger('click');
                    });
                    if (~window.location.href.indexOf('#')) {
                        var tab = window.location.href.split('#');
                        tab = tab[tab.length - 1];
                        $tabsLink.filter('[href="#' + tab + '"]').trigger('click');
                    }
                    $('.Site').on('click', 'a', function () {
                        var $this = $(this), tab = $this.attr('href').replace(window.location.pathname, '');
                        if (~$this.attr('href').indexOf(window.location.pathname)) {
                            $tabsLink.filter('[href="' + tab + '"]').trigger('click');
                        }
                    });
                }
            };
        };
        Tabs().init();
        var Tags = function () {
            return {
                init: function () {
                }
            };
        };
        Tags().init();

        var Topup = function () {
            return {
                init: function () {
                    $('form.Topup').on('submit', function (e) {
                        var $this = $(this);
                        e.preventDefault();
                        Login().postData('/payment', {
                            hash: $this.data('sendHash'),
                            sum: $this.find('[name="sum"]').val(),
                            time: new Date().getTime()
                        }, function (result) {
                            $('.Topup-wrap').append('<div class="form-group">' + '<div class="Topup-success">' + 'Оплата прошла успешно' + '</div>' + '</div>')
                        })
                    });
                }
            };
        };
        Topup().init();


        var Transactions = function () {
            var transactionTemplate = function (transaction) {
                var date = new Date(+transaction.time),
                    month = 'января,февраля,марта,апреля,мая,июня,июля,августа,сентября,октября,ноября,декабря'.split(',');
                return '<tr>\n' + '                      <td>' +
                    date.getDate() + ' ' +
                    (month[date.getMonth()]) + ' ' +
                    date.getFullYear() + ' ' +
                    date.getHours() + ':' +
                    date.getMinutes() + '                      </td>\n' + '                      <td><span class="Transactions_textSuccess">\n' + '                          <strong>' + transaction.value + ' р.\n' + '                          </strong></span>\n' + '                      </td>\n' + '                      <td>' + transaction.description + '                      </td>\n' + '                    </tr>';
            }

            function getData(address, data) {
                var scroll = $(window).scrollTop();
                $.ajax({
                    url: address,
                    type: 'GET',
                    dataType: 'json',
                    data: data,
                    complete: function (result) {
                        if (result.status === 200) {
                            var data = '';
                            result.responseJSON.transactions.forEach(function (transaction) {
                                data += transactionTemplate(transaction);
                            });
                            $('.Transactions tbody').append(data);
                            $(window).scrollTop(scroll);
                        } else {
                            alert('Ошибка ' + result.status);
                        }
                    }
                });
            }

            return {
                init: function () {
                    $('.Transactions-get').on('click', function (e) {
                        var $this = $(this);
                        e.preventDefault();
                        getData('/transactions', {
                            sort: $this.data('transactionsort'),
                            offset: $this.data('transactionoffset'),
                            limit: $this.data('transactionlimit')
                        })
                    });
                }
            };
        };
        Transactions().init();
    });
})(jQuery);