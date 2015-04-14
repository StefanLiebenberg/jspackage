(function (win, doc) {
    try {
        if (doc.createEvent) {
            
            var event = doc.createEvent('Event');
            event.initEvent('DOMContentLoaded', false, false);
            doc.dispatchEvent(event, false);
            event = doc.createEvent('HTMLEvents');
            event.initEvent('load', false, false);
            doc.dispatchEvent(event, false);

            if (doc === win.document) {
                event = doc.createEvent('HTMLEvents');
                event.initEvent('load', false, false);
                win.dispatchEvent(event, false);
            }
        }
    } catch (e) {
        console.log('window load event failed %s', e);
    }
})(window, document);