package org.umg.carritogt;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "productos")
@Getter
@Setter
@NoArgsConstructor
public class    Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 150)
    private String nombre;

    private String descripcion;

    @NotNull
    @PositiveOrZero
    private BigDecimal precio;

    @NotNull
    @PositiveOrZero
    private Integer stock;

    @Size(max = 255)
    private String imagenUrl;

    @NotNull
    private Boolean activo = true;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime creadoEn;
}