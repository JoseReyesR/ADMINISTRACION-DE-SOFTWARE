package reclamostottus.reclamos_backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historial_seguimientos")
public class HistorialSeguimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reclamo_id", nullable = false)
    private Reclamo reclamo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id_responsable", nullable = false)
    private Usuario usuarioResponsable;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "estado_anterior_id")
    private EstadoReclamo estadoAnterior;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "estado_nuevo_id")
    private EstadoReclamo estadoNuevo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "es_interno")
    private Boolean esInterno = false;

    @Column(name = "fecha_registro", insertable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    public HistorialSeguimiento() {
    }

    // --- GETTERS Y SETTERS ---
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Reclamo getReclamo() {
        return reclamo;
    }

    public void setReclamo(Reclamo reclamo) {
        this.reclamo = reclamo;
    }

    public Usuario getUsuarioResponsable() {
        return usuarioResponsable;
    }

    public void setUsuarioResponsable(Usuario usuarioResponsable) {
        this.usuarioResponsable = usuarioResponsable;
    }

    public EstadoReclamo getEstadoAnterior() {
        return estadoAnterior;
    }

    public void setEstadoAnterior(EstadoReclamo estadoAnterior) {
        this.estadoAnterior = estadoAnterior;
    }

    public EstadoReclamo getEstadoNuevo() {
        return estadoNuevo;
    }

    public void setEstadoNuevo(EstadoReclamo estadoNuevo) {
        this.estadoNuevo = estadoNuevo;
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

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}