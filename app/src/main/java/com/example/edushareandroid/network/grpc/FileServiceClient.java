package com.example.edushareandroid.network.grpc;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.protobuf.ByteString;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import file.service.FileServiceGrpc;
import file.service.Fileservice;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class FileServiceClient {
    private static final String TAG = "FileServiceClient";

    // Configuración de conexión (cambia según tu entorno)
    private static final String GRPC_SERVER_IP = "192.168.1.215"; // IP de tu PC
    private static final int GRPC_SERVER_PORT = 50051; // Puerto gRPC

    private final ManagedChannel channel;
    private final FileServiceGrpc.FileServiceBlockingStub blockingStub;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public interface UploadCallback {
        void onSuccess(String filePath);
        void onError(Exception e);
    }

    public FileServiceClient() {
        this(createChannel());
    }

    private static ManagedChannel createChannel() {
        Log.d(TAG, "Conectando a gRPC en: " + GRPC_SERVER_IP + ":" + GRPC_SERVER_PORT);
        return ManagedChannelBuilder.forAddress(GRPC_SERVER_IP, GRPC_SERVER_PORT)
                .usePlaintext() // ¡Solo para desarrollo!
                .build();
    }

    public FileServiceClient(ManagedChannel channel) {
        this.channel = channel;
        this.blockingStub = FileServiceGrpc.newBlockingStub(channel);
    }

    public void uploadImage(Bitmap image, UploadCallback callback) {
        executor.execute(() -> {
            try {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 90, stream);
                byte[] imageBytes = stream.toByteArray();

                Fileservice.UploadRequest request = Fileservice.UploadRequest.newBuilder()
                        .setUsername("usuario")
                        .setFilename("archivo.jpg")
                        .setFiledata(ByteString.copyFrom(imageBytes))
                        .build();

                Log.d(TAG, "Subiendo imagen al servidor gRPC...");
                Fileservice.UploadResponse response = blockingStub.uploadImage(request);

                Log.d(TAG, "Imagen subida exitosamente. Ruta: " + response.getFilePath());
                callback.onSuccess(response.getFilePath());
            } catch (Exception e) {
                Log.e(TAG, "Error al subir imagen: " + e.getMessage(), e);
                callback.onError(e);
            }
        });
    }

    public void shutdown() {
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown();
        }
    }
}