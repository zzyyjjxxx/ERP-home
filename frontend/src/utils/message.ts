// React 19 compatible message API for non-component usage (e.g. axios interceptor)
// Uses antd's static message as fallback, but prefer the hook-based API when available.

type MessageApi = {
  error: (msg: string) => void;
  success: (msg: string) => void;
};

let messageApi: MessageApi | null = null;

export function setGlobalMessageApi(api: MessageApi) {
  messageApi = api;
}

export function showError(msg: string) {
  if (messageApi) {
    messageApi.error(msg);
  } else {
    import('antd').then(({ message }) => message.error(msg));
  }
}

export function showSuccess(msg: string) {
  if (messageApi) {
    messageApi.success(msg);
  } else {
    import('antd').then(({ message }) => message.success(msg));
  }
}
