package uth.pmo1.musicstorehn;

public class Usuarios {
    private String uid, correo, usuario, password, fotoUrl;

    public Usuarios() {
    }

    public Usuarios(String uid, String correo, String usuario, String password, String fotoUrl) {
        this.uid = uid;
        this.correo = correo;
        this.usuario = usuario;
        this.password = password;
        this.fotoUrl = fotoUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }
}
