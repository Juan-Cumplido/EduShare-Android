package com.example.edushareandroid.network.grpc;

import android.util.Log;

import com.example.edushareandroid.grpc.UploadRequest;
import com.example.edushareandroid.grpc.UploadResponse;
import com.example.edushareandroid.grpc.DownloadRequest;
import com.example.edushareandroid.grpc.DownloadResponse;
import com.example.edushareandroid.grpc.FileServiceGrpc;
import com.google.protobuf.ByteString;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class FileServiceClient {

    private static final String TAG = "FileServiceClient";

    private static final String SERVER_IP = "192.168.1.66";
    private static final int SERVER_PORT = 50051;

    private final ManagedChannel channel;
    private final FileServiceGrpc.FileServiceBlockingStub blockingStub;
    private final ExecutorService executor;


    public interface UploadCallback {
        void onSuccess(String filePath, String coverImagePath);

        void onError(Exception e);
    }

    public interface DownloadCallback {
        void onSuccess(byte[] fileData, String filename);

        void onError(Exception e);
    }

    public FileServiceClient() {
        this.channel = ManagedChannelBuilder
                .forAddress(SERVER_IP, SERVER_PORT)
                .usePlaintext()
                .build();
        this.blockingStub = FileServiceGrpc.newBlockingStub(channel);
        this.executor = Executors.newSingleThreadExecutor();
    }

    public void uploadImage(byte[] imageData, String username, String filename, UploadCallback callback) {
        executor.execute(() -> {
            try {
                UploadRequest request = UploadRequest.newBuilder()
                        .setUsername(username)
                        .setFilename(filename)
                        .setFiledata(ByteString.copyFrom(imageData))
                        .build();

                UploadResponse response = blockingStub.uploadImage(request);
                callback.onSuccess(response.getFilePath(), response.getCoverImagePath());

            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    public void uploadPdf(byte[] pdfData, String username, String filename, UploadCallback callback) {
        executor.execute(() -> {
            try {
                UploadRequest request = UploadRequest.newBuilder()
                        .setUsername(username)
                        .setFilename(filename)
                        .setFiledata(ByteString.copyFrom(pdfData))
                        .build();

                UploadResponse response = blockingStub.uploadPdf(request);
                callback.onSuccess(response.getFilePath(), response.getCoverImagePath());

            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    public void downloadImage(String relativePath, DownloadCallback callback) {
        executor.execute(() -> {
            try {
                DownloadRequest request = DownloadRequest.newBuilder()
                        .setRelativePath(relativePath)
                        .build();

                DownloadResponse response = blockingStub.downloadImage(request);
                callback.onSuccess(response.getFiledata().toByteArray(), response.getFilename());
            } catch (Exception e) {
                Log.e(TAG, "Error al descargar imagen", e);
                callback.onError(e);
            }
        });
    }

    public void downloadPdf(String relativePath, DownloadCallback callback) {
        executor.execute(() -> {
            try {
                DownloadRequest request = DownloadRequest.newBuilder()
                        .setRelativePath(relativePath)
                        .build();

                DownloadResponse response = blockingStub.downloadPdf(request);
                callback.onSuccess(response.getFiledata().toByteArray(), response.getFilename());
            } catch (Exception e) {
                Log.e(TAG, "Error al descargar PDF", e);
                callback.onError(e);
            }
        });
    }

    public void downloadCover(String relativePath, DownloadCallback callback) {
        executor.execute(() -> {
            try {
                DownloadRequest request = DownloadRequest.newBuilder()
                        .setRelativePath(relativePath)
                        .build();

                DownloadResponse response = blockingStub.downloadCover(request);
                callback.onSuccess(response.getFiledata().toByteArray(), response.getFilename());
            } catch (Exception e) {
                Log.e(TAG, "Error al descargar portada", e);
                callback.onError(e);
            }
        });
    }

    public void shutdown() {
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown();
        }
        executor.shutdown();
    }
}
