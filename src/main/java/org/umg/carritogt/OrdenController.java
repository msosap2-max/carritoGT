package org.umg.carritogt;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/ordenes")
public class OrdenController {

    private final OrdenRepository ordenRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;

    public OrdenController(OrdenRepository ordenRepository,
                           UsuarioRepository usuarioRepository,
                           ProductoRepository productoRepository) {
        this.ordenRepository = ordenRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public Orden crear(@Valid @RequestBody OrdenRequest request, @RequestAttribute("correo") String correo) {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .filter(Usuario::getActivo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no válido"));
        Orden orden = new Orden();
        orden.setUsuarioId(usuario.getId());
        request.items().forEach(item -> orden.getDetalles().add(construirDetalle(orden, item)));
        orden.setTotal(calcularTotal(orden));
        return ordenRepository.save(orden);
    }

    private OrdenDetalle construirDetalle(Orden orden, ItemRequest item) {
        Producto producto = productoRepository.findById(item.productoId())
                .filter(Producto::getActivo)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Producto no disponible: " + item.productoId()));
        OrdenDetalle detalle = new OrdenDetalle();
        detalle.setOrden(orden);
        detalle.setProductoId(producto.getId());
        detalle.setCantidad(item.cantidad());
        detalle.setPrecioUnitario(producto.getPrecio());
        return detalle;
    }

    private BigDecimal calcularTotal(Orden orden) {
        return orden.getDetalles().stream()
                .map(detalle -> detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(detalle.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public record OrdenRequest(@NotEmpty @Valid List<ItemRequest> items) {
    }

    public record ItemRequest(@NotNull Long productoId, @NotNull @Positive Integer cantidad) {
    }
}