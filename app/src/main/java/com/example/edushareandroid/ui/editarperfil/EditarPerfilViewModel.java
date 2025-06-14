package com.example.edushareandroid.ui.editarperfil;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.edushareandroid.model.base_de_datos.ActualizarPerfil;
import com.example.edushareandroid.model.base_de_datos.AvatarRequest;
import com.example.edushareandroid.model.base_de_datos.Institucion;
import com.example.edushareandroid.ui.perfil.UsuarioPerfil;
import com.example.edushareandroid.network.grpc.FileServiceClient;
import com.example.edushareandroid.utils.ImageUtil;

import java.util.List;

public class EditarPerfilViewModel extends ViewModel {

    private final MutableLiveData<List<Institucion>> instituciones = new MutableLiveData<>();
    private final MutableLiveData<Boolean> resultadoActualizacion = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<UsuarioPerfil> perfilLiveData = new MutableLiveData<>();
    private final MutableLiveData<Bitmap> imagenPerfilLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final EditarPerfilRepository repository = new EditarPerfilRepository();
    private final MutableLiveData<Boolean> resultadoActualizacionAvatar = new MutableLiveData<>();

    public LiveData<Boolean> getResultadoActualizacionAvatar() {
        return resultadoActualizacionAvatar;
    }

    public LiveData<List<Institucion>> getInstituciones() {
        return instituciones;
    }

    public LiveData<Boolean> getResultadoActualizacion() {
        return resultadoActualizacion;
    }

    public LiveData<Bitmap> getImagenPerfilLiveData() {
        return imagenPerfilLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public LiveData<UsuarioPerfil> getPerfilLiveData() {
        return perfilLiveData;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void cargarInstituciones(String nivelEducativo) {
        repository.obtenerInstituciones(nivelEducativo, new EditarPerfilRepository.InstitucionesCallback() {
            @Override
            public void onSuccess(List<Institucion> datos) {
                instituciones.postValue(datos);
            }

            @Override
            public void onError(String mensaje) {
                error.postValue(mensaje);
            }
        });
    }

    public void descargarImagenPerfil() {
        UsuarioPerfil perfil = perfilLiveData.getValue();
        if (perfil == null || perfil.getFotoPerfil() == null || perfil.getFotoPerfil().isEmpty()) {
            errorLiveData.setValue("No se encontró la ruta de la imagen.");
            return;
        }

        String ruta = perfil.getFotoPerfil();
        FileServiceClient client = new FileServiceClient();
        client.downloadImage(ruta, new FileServiceClient.DownloadCallback() {
            @Override
            public void onSuccess(byte[] fileData, String filename) {
                Bitmap bitmap = ImageUtil.binaryToBitmap(fileData);
                imagenPerfilLiveData.postValue(bitmap);
                client.shutdown();
            }

            @Override
            public void onError(Exception e) {
                errorLiveData.postValue("Error al descargar imagen: " + e.getMessage());
                client.shutdown();
            }
        });
    }

    public void setPerfil(UsuarioPerfil perfil) {
        perfilLiveData.setValue(perfil);
    }

    public void actualizarPerfil(String token, ActualizarPerfil perfil) {
        repository.actualizarPerfil(token, perfil, new EditarPerfilRepository.ActualizacionCallback() {
            @Override
            public void onSuccess(String mensaje) {
                resultadoActualizacion.postValue(true);
            }

            @Override
            public void onError(String mensaje) {
                error.postValue(mensaje);
                resultadoActualizacion.postValue(false);
            }
        });
    }


    public void subirImagenGrpc(byte[] imageData, String username, String filename, FileServiceClient.UploadCallback callback) {
        Log.d("EditarPerfilVM", "Iniciando subida de imagen grpc: filename=" + filename + ", username=" + username);
        FileServiceClient grpcClient = new FileServiceClient();
        grpcClient.uploadImage(imageData, username, filename, new FileServiceClient.UploadCallback() {
            @Override
            public void onSuccess(String filePath, String coverImagePath) {
                Log.d("EditarPerfilVM", "Subida grpc exitosa. filePath: " + filePath + ", coverImagePath: " + coverImagePath);
                callback.onSuccess(filePath, coverImagePath);
            }

            @Override
            public void onError(Exception e) {
                Log.e("EditarPerfilVM", "Error en subida grpc: " + e.getMessage(), e);
                callback.onError(e);
            }
        });
    }

    public void actualizarAvatar(String token, AvatarRequest avatarRequest) {
        repository.actualizarAvatar(token, avatarRequest, new EditarPerfilRepository.ActualizacionCallback() {
            @Override
            public void onSuccess(String mensaje) {
                resultadoActualizacionAvatar.postValue(true);
            }

            @Override
            public void onError(String mensaje) {
                error.postValue(mensaje);
                resultadoActualizacionAvatar.postValue(false);
            }
        });
    }

}
