const baseUrl = '/_native/api/call';

console.log('[GEO-BRIDGE] index.js loaded');

function debugLog(...args) {
    console.log('[GEO-BRIDGE]', ...args);
}

async function bridgeCall(method, params = {}) {
    debugLog('bridgeCall:start', { method, params, baseUrl });

    const response = await fetch(baseUrl, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
            'X-Requested-With': 'XMLHttpRequest',
        },
        body: JSON.stringify({
            method,
            params,
        }),
    });

    debugLog('bridgeCall:response-status', response.status);

    let data = null;

    try {
        data = await response.json();
        debugLog('bridgeCall:response-json', data);
    } catch (e) {
        debugLog('bridgeCall:invalid-json', e);
        throw new Error(`Native bridge returned invalid JSON for ${method}`);
    }

    if (!response.ok) {
        debugLog('bridgeCall:response-not-ok', data);
        throw new Error(data?.message || `Native bridge error: ${response.status}`);
    }

    return data;
}

export async function getCurrentPosition(highAccuracy = true) {
    debugLog('getCurrentPosition called', { highAccuracy });
    return bridgeCall('Geolocation.GetCurrentPosition', { highAccuracy });
}

export async function requestPermission() {
    debugLog('requestPermission called');
    return bridgeCall('Geolocation.RequestPermission', {});
}

const Geolocation = {
    GetCurrentPosition(options = {}) {
        const highAccuracy =
            typeof options === 'boolean'
                ? options
                : (options?.highAccuracy ?? true);

        debugLog('Geolocation.GetCurrentPosition wrapper', { options, highAccuracy });
        return getCurrentPosition(highAccuracy);
    },

    RequestPermission() {
        debugLog('Geolocation.RequestPermission wrapper');
        return requestPermission();
    },
};

if (typeof window !== 'undefined') {
    window.NativePHP = window.NativePHP || {};
    window.NativePHP.Geolocation = Geolocation;
    debugLog('window.NativePHP.Geolocation registered', window.NativePHP.Geolocation);
} else {
    debugLog('window is undefined');
}

export default Geolocation;