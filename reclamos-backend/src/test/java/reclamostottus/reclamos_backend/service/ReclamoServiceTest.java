package reclamostottus.reclamos_backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.MockMultipartFile;
import reclamostottus.reclamos_backend.dto.ReclamoRequestDTO;
import reclamostottus.reclamos_backend.model.*;
import reclamostottus.reclamos_backend.repository.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ReclamoServiceTest {

    @Mock
    private ReclamoRepository reclamoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EvidenciaRepository evidenciaRepository;

    @Mock
    private EstadoReclamoRepository estadoRepository;

    @Mock
    private PrioridadRepository prioridadRepository;

    @Mock
    private CatalogoMotivoRepository motivoRepository;

    @Mock
    private TiendaRepository tiendaRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ReclamoService reclamoService;

    private Usuario usuarioExistenteActivo;

    @BeforeEach
    void setUp() {
        Rol rol = new Rol();
        rol.setId(1);
        rol.setNombre("ROLE_CLIENTE");

        usuarioExistenteActivo = new Usuario();
        usuarioExistenteActivo.setId(1);
        usuarioExistenteActivo.setNumeroDocumento("12345678");
        usuarioExistenteActivo.setCorreo("cliente@correo.com");
        usuarioExistenteActivo.setRol(rol);
        usuarioExistenteActivo.setIsActive(true);

        when(usuarioRepository.findByNumeroDocumento("12345678")).thenReturn(Optional.of(usuarioExistenteActivo));

        // Simula que la BD asigna un id autogenerado al guardar el reclamo
        when(reclamoRepository.save(any(Reclamo.class))).thenAnswer(invocation -> {
            Reclamo reclamo = invocation.getArgument(0);
            reclamo.setId(100);
            return reclamo;
        });

        CatalogoMotivo motivo = new CatalogoMotivo();
        motivo.setId(1);
        motivo.setNombre("Producto dañado");
        when(motivoRepository.findById(1)).thenReturn(Optional.of(motivo));
    }

    private ReclamoRequestDTO dtoValido() {
        ReclamoRequestDTO dto = new ReclamoRequestDTO();
        dto.setTipoDocumento("DNI");
        dto.setNumeroDocumento("12345678");
        dto.setNombres("Cliente");
        dto.setApellidos("Prueba");
        dto.setCorreo("cliente@correo.com");
        dto.setCelular("999999999");
        dto.setTipoSolicitud("Reclamo");
        dto.setCanalCompra("Online");
        dto.setMotivo("1");
        dto.setProducto("Licuadora");
        dto.setDescripcion("Llegó dañado");
        return dto;
    }

    // ==================== listarTodos / buscarPorCodigo ====================

    @Test
    void listarTodosDelegaEnElRepositorio() {
        List<Reclamo> lista = List.of(new Reclamo());
        when(reclamoRepository.findAll()).thenReturn(lista);

        assertThat(reclamoService.listarTodos()).isSameAs(lista);
    }

    @Test
    void buscarPorCodigoDelegaEnElRepositorio() {
        Reclamo reclamo = new Reclamo();
        when(reclamoRepository.findByCodigoSeguimiento("REQ-2026-0001")).thenReturn(Optional.of(reclamo));

        assertThat(reclamoService.buscarPorCodigo("REQ-2026-0001")).contains(reclamo);
    }

    // ==================== seguimientoSeguroInvitado ====================

    @Test
    void seguimientoSeguroInvitadoDevuelveElReclamoCuandoElDniCoincide() {
        Usuario usuario = new Usuario();
        usuario.setNumeroDocumento("12345678");
        Reclamo reclamo = new Reclamo();
        reclamo.setUsuario(usuario);
        when(reclamoRepository.findByCodigoSeguimiento("REQ-2026-0001")).thenReturn(Optional.of(reclamo));

        Optional<Reclamo> resultado = reclamoService.seguimientoSeguroInvitado("REQ-2026-0001", "12345678");

        assertThat(resultado).contains(reclamo);
    }

    @Test
    void seguimientoSeguroInvitadoDevuelveVacioCuandoElDniNoCoincide() {
        Usuario usuario = new Usuario();
        usuario.setNumeroDocumento("12345678");
        Reclamo reclamo = new Reclamo();
        reclamo.setUsuario(usuario);
        when(reclamoRepository.findByCodigoSeguimiento("REQ-2026-0001")).thenReturn(Optional.of(reclamo));

        Optional<Reclamo> resultado = reclamoService.seguimientoSeguroInvitado("REQ-2026-0001", "00000000");

        assertThat(resultado).isEmpty();
    }

    @Test
    void seguimientoSeguroInvitadoDevuelveVacioCuandoElCodigoNoExiste() {
        when(reclamoRepository.findByCodigoSeguimiento("NO-EXISTE")).thenReturn(Optional.empty());

        Optional<Reclamo> resultado = reclamoService.seguimientoSeguroInvitado("NO-EXISTE", "12345678");

        assertThat(resultado).isEmpty();
    }

    // ==================== obtenerMisCasos ====================

    @Test
    void obtenerMisCasosFiltraPorCorreoDelUsuario() {
        Usuario usuarioA = new Usuario();
        usuarioA.setCorreo("a@correo.com");
        Usuario usuarioB = new Usuario();
        usuarioB.setCorreo("b@correo.com");

        Reclamo reclamoA = new Reclamo();
        reclamoA.setUsuario(usuarioA);
        Reclamo reclamoB = new Reclamo();
        reclamoB.setUsuario(usuarioB);

        when(reclamoRepository.findAll()).thenReturn(List.of(reclamoA, reclamoB));

        List<Reclamo> resultado = reclamoService.obtenerMisCasos("a@correo.com");

        assertThat(resultado).containsExactly(reclamoA);
    }

    // ==================== registrarReclamo: usuario ====================

    @Test
    void registrarReclamoConUsuarioExistenteActivoFuncionaCorrectamente() {
        Reclamo resultado = reclamoService.registrarReclamo(dtoValido(), null);

        assertThat(resultado.getUsuario()).isSameAs(usuarioExistenteActivo);
        assertThat(resultado.getCodigoSeguimiento()).startsWith("REQ-2026-");
        assertThat(resultado.getCategoria().getId()).isEqualTo(1); // categoría por defecto
        assertThat(resultado.getPrioridad().getId()).isEqualTo(2);
        assertThat(resultado.getEstado().getId()).isEqualTo(1);
        assertThat(resultado.getFechaVencimiento()).isNotNull();
        verify(emailService, times(1)).enviarCorreoRegistro(resultado);
        verifyNoInteractions(evidenciaRepository);
    }

    @Test
    void registrarReclamoConUsuarioInactivoLanzaExcepcion() {
        usuarioExistenteActivo.setIsActive(false);

        assertThatThrownBy(() -> reclamoService.registrarReclamo(dtoValido(), null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("restringida");

        verify(reclamoRepository, never()).save(any());
    }

    @Test
    void registrarReclamoConUsuarioNuevoLoCreaConRolInvitadoYActivo() {
        when(usuarioRepository.findByNumeroDocumento("87654321")).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuario = invocation.getArgument(0);
            usuario.setId(50);
            return usuario;
        });

        ReclamoRequestDTO dto = dtoValido();
        dto.setNumeroDocumento("87654321");
        dto.setCorreo("nuevo@correo.com");

        Reclamo resultado = reclamoService.registrarReclamo(dto, null);

        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository, times(1)).save(usuarioCaptor.capture());

        Usuario usuarioCreado = usuarioCaptor.getValue();
        assertThat(usuarioCreado.getNumeroDocumento()).isEqualTo("87654321");
        assertThat(usuarioCreado.getRol().getId()).isEqualTo(3);
        assertThat(usuarioCreado.getIsActive()).isTrue();
        assertThat(usuarioCreado.getPassword()).isNotBlank();
        assertThat(resultado.getUsuario()).isSameAs(usuarioCreado);
    }

    // ==================== registrarReclamo: tienda ====================

    @Test
    void registrarReclamoSinTiendaNoLaAsigna() {
        ReclamoRequestDTO dto = dtoValido();
        dto.setTienda(null);

        Reclamo resultado = reclamoService.registrarReclamo(dto, null);

        assertThat(resultado.getTienda()).isNull();
        verifyNoInteractions(tiendaRepository);
    }

    @Test
    void registrarReclamoConTiendaValidaLaAsignaCorrectamente() {
        Tienda tienda = new Tienda();
        tienda.setId(3);
        tienda.setNombre("Tottus San Miguel");
        when(tiendaRepository.findById(3)).thenReturn(Optional.of(tienda));

        ReclamoRequestDTO dto = dtoValido();
        dto.setTienda("3");

        Reclamo resultado = reclamoService.registrarReclamo(dto, null);

        assertThat(resultado.getTienda()).isSameAs(tienda);
    }

    @Test
    void registrarReclamoConTiendaInexistenteLanzaExcepcion() {
        when(tiendaRepository.findById(99)).thenReturn(Optional.empty());

        ReclamoRequestDTO dto = dtoValido();
        dto.setTienda("99");

        assertThatThrownBy(() -> reclamoService.registrarReclamo(dto, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("La tienda seleccionada no existe");
    }

    @Test
    void registrarReclamoConTiendaNoNumericaLanzaExcepcion() {
        ReclamoRequestDTO dto = dtoValido();
        dto.setTienda("abc");

        assertThatThrownBy(() -> reclamoService.registrarReclamo(dto, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("El ID de la tienda es inválido.");
    }

    // ==================== registrarReclamo: motivo ====================

    @Test
    void registrarReclamoConMotivoInexistenteLanzaExcepcion() {
        when(motivoRepository.findById(99)).thenReturn(Optional.empty());

        ReclamoRequestDTO dto = dtoValido();
        dto.setMotivo("99");

        assertThatThrownBy(() -> reclamoService.registrarReclamo(dto, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("El motivo seleccionado no existe");
    }

    @Test
    void registrarReclamoConMotivoNoNumericoLanzaExcepcion() {
        ReclamoRequestDTO dto = dtoValido();
        dto.setMotivo("abc");

        assertThatThrownBy(() -> reclamoService.registrarReclamo(dto, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("El ID del motivo es inválido.");
    }

    @Test
    void registrarReclamoSinMotivoLanzaExcepcion() {
        ReclamoRequestDTO dto = dtoValido();
        dto.setMotivo(null);

        assertThatThrownBy(() -> reclamoService.registrarReclamo(dto, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Debe seleccionar un motivo obligatoriamente.");
    }

    @Test
    void registrarReclamoConMotivoVacioLanzaExcepcion() {
        ReclamoRequestDTO dto = dtoValido();
        dto.setMotivo("");

        assertThatThrownBy(() -> reclamoService.registrarReclamo(dto, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Debe seleccionar un motivo obligatoriamente.");
    }

    // ==================== registrarReclamo: fechaCompra ====================

    @Test
    void registrarReclamoConFechaCompraValidaLaAsigna() {
        ReclamoRequestDTO dto = dtoValido();
        dto.setFechaCompra("2026-01-15");

        Reclamo resultado = reclamoService.registrarReclamo(dto, null);

        assertThat(resultado.getFechaCompra().toString()).isEqualTo("2026-01-15");
    }

    @Test
    void registrarReclamoSinFechaCompraLaDejaNula() {
        ReclamoRequestDTO dto = dtoValido();
        dto.setFechaCompra(null);

        Reclamo resultado = reclamoService.registrarReclamo(dto, null);

        assertThat(resultado.getFechaCompra()).isNull();
    }

    @Test
    void registrarReclamoConFechaCompraVaciaLaDejaNula() {
        ReclamoRequestDTO dto = dtoValido();
        dto.setFechaCompra("");

        Reclamo resultado = reclamoService.registrarReclamo(dto, null);

        assertThat(resultado.getFechaCompra()).isNull();
    }

    // ==================== registrarReclamo: categoria ====================

    @Test
    void registrarReclamoConCategoriaValidaLaAsigna() {
        ReclamoRequestDTO dto = dtoValido();
        dto.setCategoria("5");

        Reclamo resultado = reclamoService.registrarReclamo(dto, null);

        assertThat(resultado.getCategoria().getId()).isEqualTo(5);
    }

    @Test
    void registrarReclamoSinCategoriaUsaLaCategoriaPorDefecto() {
        ReclamoRequestDTO dto = dtoValido();
        dto.setCategoria(null);

        Reclamo resultado = reclamoService.registrarReclamo(dto, null);

        assertThat(resultado.getCategoria().getId()).isEqualTo(1);
    }

    @Test
    void registrarReclamoConCategoriaNoNumericaLanzaExcepcion() {
        ReclamoRequestDTO dto = dtoValido();
        dto.setCategoria("abc");

        assertThatThrownBy(() -> reclamoService.registrarReclamo(dto, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("El ID de la categoría es inválido.");
    }

    // ==================== registrarReclamo: archivo/evidencia ====================

    @Test
    void registrarReclamoSinArchivoNoGuardaEvidencia() {
        reclamoService.registrarReclamo(dtoValido(), null);

        verifyNoInteractions(evidenciaRepository);
    }

    @Test
    void registrarReclamoConArchivoVacioNoGuardaEvidencia() {
        MockMultipartFile archivoVacio = new MockMultipartFile("archivo", "vacio.jpg", "image/jpeg", new byte[0]);

        reclamoService.registrarReclamo(dtoValido(), archivoVacio);

        verifyNoInteractions(evidenciaRepository);
    }

    @Test
    void registrarReclamoConArchivoValidoGuardaLaEvidenciaConLosDatosCorrectos() {
        byte[] contenido = "contenido-de-prueba".getBytes();
        MockMultipartFile archivo = new MockMultipartFile("archivo", "foto.jpg", "image/jpeg", contenido);

        try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {
            Path directorio = Paths.get("uploads/");
            filesMock.when(() -> Files.exists(directorio)).thenReturn(true);
            filesMock.when(() -> Files.copy(any(java.io.InputStream.class), any(Path.class),
                    eq(StandardCopyOption.REPLACE_EXISTING))).thenReturn(0L);

            reclamoService.registrarReclamo(dtoValido(), archivo);

            filesMock.verify(() -> Files.createDirectories(any(Path.class)), never());
        }

        ArgumentCaptor<Evidencia> evidenciaCaptor = ArgumentCaptor.forClass(Evidencia.class);
        verify(evidenciaRepository, times(1)).save(evidenciaCaptor.capture());

        Evidencia evidenciaGuardada = evidenciaCaptor.getValue();
        assertThat(evidenciaGuardada.getNombreArchivo()).isEqualTo("foto.jpg");
        assertThat(evidenciaGuardada.getRutaArchivo()).isEqualTo("/uploads/foto.jpg");
        assertThat(evidenciaGuardada.getTipoArchivo()).isEqualTo("image/jpeg");
        assertThat(evidenciaGuardada.getTamanioMb()).isNotNull();
    }

    @Test
    void registrarReclamoCreaElDirectorioDeUploadsCuandoNoExiste() {
        byte[] contenido = "contenido".getBytes();
        MockMultipartFile archivo = new MockMultipartFile("archivo", "foto2.jpg", "image/png", contenido);

        try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {
            Path directorio = Paths.get("uploads/");
            filesMock.when(() -> Files.exists(directorio)).thenReturn(false);
            filesMock.when(() -> Files.createDirectories(directorio)).thenReturn(directorio);
            filesMock.when(() -> Files.copy(any(java.io.InputStream.class), any(Path.class),
                    eq(StandardCopyOption.REPLACE_EXISTING))).thenReturn(0L);

            reclamoService.registrarReclamo(dtoValido(), archivo);

            filesMock.verify(() -> Files.createDirectories(directorio), times(1));
        }

        verify(evidenciaRepository, times(1)).save(any(Evidencia.class));
    }

    @Test
    void registrarReclamoConErrorAlGuardarElArchivoLanzaExcepcionEnvuelta() {
        byte[] contenido = "contenido".getBytes();
        MockMultipartFile archivo = new MockMultipartFile("archivo", "foto3.jpg", "image/jpeg", contenido);

        try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {
            Path directorio = Paths.get("uploads/");
            filesMock.when(() -> Files.exists(directorio)).thenReturn(true);
            filesMock.when(() -> Files.copy(any(java.io.InputStream.class), any(Path.class),
                    eq(StandardCopyOption.REPLACE_EXISTING)))
                    .thenThrow(new java.io.UncheckedIOException(new java.io.IOException("Disco lleno")));

            assertThatThrownBy(() -> reclamoService.registrarReclamo(dtoValido(), archivo))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("hubo un error al guardar la imagen física");
        }

        verifyNoInteractions(evidenciaRepository);
    }

    // ==================== actualizarEstadoReclamo ====================

    @Test
    void actualizarEstadoReclamoCambiaElEstadoYEnviaCorreo() {
        Reclamo reclamo = new Reclamo();
        reclamo.setId(1);
        reclamo.setCodigoSeguimiento("REQ-2026-0002");
        when(reclamoRepository.findById(1)).thenReturn(Optional.of(reclamo));

        EstadoReclamo nuevoEstado = new EstadoReclamo();
        nuevoEstado.setId(2);
        nuevoEstado.setNombre("En Proceso");
        when(estadoRepository.findById(2)).thenReturn(Optional.of(nuevoEstado));

        Reclamo resultado = reclamoService.actualizarEstadoReclamo(1, 2);

        assertThat(resultado.getEstado()).isSameAs(nuevoEstado);
        verify(emailService, times(1)).enviarCorreoActualizacion(eq("REQ-2026-0002"), contains("En Proceso"));
    }

    @Test
    void actualizarEstadoReclamoLanzaExcepcionCuandoElReclamoNoExiste() {
        when(reclamoRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reclamoService.actualizarEstadoReclamo(1, 2))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Reclamo no encontrado");

        verifyNoInteractions(emailService);
    }

    @Test
    void actualizarEstadoReclamoLanzaExcepcionCuandoElEstadoNoExiste() {
        when(reclamoRepository.findById(1)).thenReturn(Optional.of(new Reclamo()));
        when(estadoRepository.findById(2)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reclamoService.actualizarEstadoReclamo(1, 2))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Estado no encontrado");

        verifyNoInteractions(emailService);
    }

    // ==================== actualizarPrioridadReclamo ====================

    @Test
    void actualizarPrioridadReclamoCambiaLaPrioridad() {
        Reclamo reclamo = new Reclamo();
        reclamo.setId(1);
        when(reclamoRepository.findById(1)).thenReturn(Optional.of(reclamo));

        Prioridad nuevaPrioridad = new Prioridad();
        nuevaPrioridad.setId(3);
        when(prioridadRepository.findById(3)).thenReturn(Optional.of(nuevaPrioridad));

        Reclamo resultado = reclamoService.actualizarPrioridadReclamo(1, 3);

        assertThat(resultado.getPrioridad()).isSameAs(nuevaPrioridad);
    }

    @Test
    void actualizarPrioridadReclamoLanzaExcepcionCuandoElReclamoNoExiste() {
        when(reclamoRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reclamoService.actualizarPrioridadReclamo(1, 3))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Reclamo no encontrado");
    }

    @Test
    void actualizarPrioridadReclamoLanzaExcepcionCuandoLaPrioridadNoExiste() {
        when(reclamoRepository.findById(1)).thenReturn(Optional.of(new Reclamo()));
        when(prioridadRepository.findById(3)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reclamoService.actualizarPrioridadReclamo(1, 3))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Prioridad no encontrada");
    }
}
