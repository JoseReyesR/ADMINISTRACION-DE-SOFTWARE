package reclamostottus.reclamos_backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "motivos") // O el nombre exacto que le hayas puesto a tu tabla de motivos en MySQL
public class Motivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(name = "tipo_solicitud", length = 50)
    private String tipoSolicitud; // Para filtrar si es Reclamo o Queja

    public Motivo() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipoSolicitud() {
        return tipoSolicitud;
    }

    public void setTipoSolicitud(String tipoSolicitud) {
        this.tipoSolicitud = tipoSolicitud;
    }
}