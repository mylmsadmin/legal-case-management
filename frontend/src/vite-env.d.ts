/// <reference types="vite/client" />

interface ImportMetaEnv {
    readonly VITE_API_URL: string;
    // Add other VITE_ prefixed env variables here
}

interface ImportMeta {
    readonly env: ImportMetaEnv;
}