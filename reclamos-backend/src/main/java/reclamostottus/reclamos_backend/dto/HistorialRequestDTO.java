package reclamostottus.reclamos_backend.dto;

public class HistorialRequestDTO {
    private Integer reclamoId;
    private Integer usuarioResponsableId;
    private Integer estadoNuevoId; // Opcional: solo si cambian el estado
    private String comentario;
    private Boolean esInterno;

    // Getters y Setters
    public Integer getReclamoId() {
        return reclamoId;
    }

    public void setReclamoId(Integer reclamoId) {
        this.reclamoId = reclamoId;
    }

    public Integer getUsuarioResponsableId() {
        return usuarioResponsableId;
    }

    public void setUsuarioResponsableId(Integer usuarioResponsableId) {
        this.usuarioResponsableId = usuarioResponsableId;
    }

    public Integer getEstadoNuevoId() {
        return estadoNuevoId;
    }

    public void setEstadoNuevoId(Integer estadoNuevoId) {
        this.estadoNuevoId = estadoNuevoId;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Boolean getEsInterno() {
        return esInterno;
    }

    public void setEsInterno(Boolean esInterno) {
        this.esInterno = esInterno;
    }
}