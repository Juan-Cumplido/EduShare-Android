package com.example.edushareandroid.model.base_de_datos;

public class AvatarRequest {
    private DatosAvatar datos;

    public AvatarRequest(String fotoPerfil) {
        this.datos = new DatosAvatar(fotoPerfil);
    }

    public DatosAvatar getDatos() {
        return datos;
    }

    public void setDatos(DatosAvatar datos) {
        this.datos = datos;
    }

    public static class DatosAvatar {
        private String fotoPerfil;

        public DatosAvatar(String fotoPerfil) {
            this.fotoPerfil = fotoPerfil;
        }

        public String getFotoPerfil() {
            return fotoPerfil;
        }

        public void setFotoPerfil(String fotoPerfil) {
            this.fotoPerfil = fotoPerfil;
        }
    }
}
