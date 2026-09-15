package reclamostottus.reclamos_backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reclamos")
public class Reclamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "codigo_seguimiento", nullable = false, unique = true, length = 20)
    private String codigoSeguimiento;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "prioridad_id", nullable = false)
    private Prioridad prioridad;

    @ManyToOne
    @JoinColumn(name = "estado_id", nullable = false)
    private EstadoReclamo estado;

    // --- NUEVOS CAMPOS DEL FRONTEND --- // --- NUEVOS CAMPOS ADAPTADOS A TU SQL
    // ---
    @Column(name = "tipo_solicitud", nullable = false, length = 50)
    private String tipoSolicitud;

    @Column(name = "canal_compra", length = 50)
    private String canalCompra;

    // RELACIÓN CON LA NUEVA TABLA TIENDAS
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tienda_id", nullable = false)
    private Tienda tienda;
    // ----------------------------------
    // RELACIÓN CON LA NUEVA TABLA MOTIVOS
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "motivo_id", nullable = false)
    private CatalogoMotivo motivo;

    @Column(name = "numero_boleta_pedido", length = 100)
    private String numeroBoletaPedido;

    @Column(name = "fecha_compra")
    private LocalDate fechaCompra;

    @Column(name = "producto_implicado", length = 255)
    private String productoImplicado;

    @Column(name = "descripcion_caso", nullable = false, columnDefinition = "TEXT")
    private String descripcionCaso;

    @Column(name = "fecha_registro", insertable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDateTime fechaVencimiento;

    public Reclamo() {
    }

    // --- Getters y Setters ---
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCodigoSeguimiento() {
        return codigoSeguimiento;
    }

    public void setCodigoSeguimiento(String codigoSeguimiento) {
        this.codigoSeguimiento = codigoSeguimiento;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(Prioridad prioridad) {
        this.prioridad = prioridad;
    }

    public EstadoReclamo getEstado() {
        return estado;
    }

    public void setEstado(EstadoReclamo estado) {
        this.estado = estado;
    }

    public String getTipoSolicitud() {
        return tipoSolicitud;
    }

    public void setTipoSolicitud(String tipoSolicitud) {
        this.tipoSolicitud = tipoSolicitud;
    }

    public String getCanalCompra() {
        return canalCompra;
    }

    public void setCanalCompra(String canalCompra) {
        this.canalCompra = canalCompra;
    }

    // agregado nuevo
    public Tienda getTienda() {
        return tienda;
    }

    public void setTienda(Tienda tienda) {
        this.tienda = tienda;
    }

    public CatalogoMotivo getMotivo() {
        return motivo;
    }

    public void setMotivo(CatalogoMotivo motivo) {
        this.motivo = motivo;
    }

    // ----------------------------------------------------------
    public String getNumeroBoletaPedido() {
        return numeroBoletaPedido;
    }

    public void setNumeroBoletaPedido(String numeroBoletaPedido) {
        this.numeroBoletaPedido = numeroBoletaPedido;
    }

    public LocalDate getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(LocalDate fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public String getProductoImplicado() {
        return productoImplicado;
    }

    public void setProductoImplicado(String productoImplicado) {
        this.productoImplicado = productoImplicado;
    }

    public String getDescripcionCaso() {
        return descripcionCaso;
    }

    public void setDescripcionCaso(String descripcionCaso) {
        this.descripcionCaso = descripcionCaso;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public LocalDateTime getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDateTime fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }
}