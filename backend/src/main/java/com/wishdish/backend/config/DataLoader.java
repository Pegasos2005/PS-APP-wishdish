package com.wishdish.backend.config;

import com.wishdish.backend.entity.Categoria;
import com.wishdish.backend.entity.Producto;
import com.wishdish.backend.repository.CategoriaRepository;
import com.wishdish.backend.repository.ProductoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

/**
 * Configuración para cargar datos de prueba en la base de datos.
 *
 * Esta clase se ejecuta automáticamente al iniciar Spring Boot y pobla la BD
 * con datos de ejemplo si la base de datos está vacía.
 *
 * IMPORTANTE: Solo carga datos si NO existen categorías en la BD.
 * Esto evita duplicados al reiniciar la aplicación.
 *
 * Para desactivar esta carga automática:
 * - Comentar la anotación @Bean
 * - O usar el perfil de Spring (spring.profiles.active != dev)
 */
@Configuration
public class DataLoader {

    /**
     * Bean que se ejecuta al iniciar la aplicación.
     * Carga datos de prueba si la BD está vacía.
     */
    @Bean
    CommandLineRunner initDatabase(
            CategoriaRepository categoriaRepository,
            ProductoRepository productoRepository) {

        return args -> {
            // Solo cargar datos si la BD está vacía
            if (categoriaRepository.count() > 0) {
                System.out.println("✅ Base de datos ya contiene datos. Omitiendo carga inicial.");
                return;
            }

            System.out.println("🔄 Cargando datos de prueba en la base de datos...");

            // ===========================
            // CREAR CATEGORÍAS
            // ===========================
            Categoria entrantes = categoriaRepository.save(
                    new Categoria("Entrantes", "Platos ligeros para comenzar tu comida")
            );

            Categoria hamburguesas = categoriaRepository.save(
                    new Categoria("Hamburguesas", "Nuestras hamburguesas gourmet hechas con carne 100% de vaca gallega")
            );

            Categoria guarniciones = categoriaRepository.save(
                    new Categoria("Guarniciones", "Acompañamientos perfectos para tu plato principal")
            );

            Categoria postres = categoriaRepository.save(
                    new Categoria("Postres", "Dulces caseros para terminar con un toque especial")
            );

            Categoria bebidas = categoriaRepository.save(
                    new Categoria("Bebidas", "Refrescos, aguas y bebidas para acompañar tu comida")
            );

            System.out.println("   ✓ 5 categorías creadas");

            // ===========================
            // CREAR PRODUCTOS - ENTRANTES
            // ===========================
            productoRepository.save(new Producto(
                    "Nachos con Queso",
                    "Nachos crujientes con mezcla de quesos y guacamole.",
                    new BigDecimal("8.90"),
                    entrantes
            )).setImagen("assets/nachos.jpg");

            productoRepository.save(new Producto(
                    "Tequeños",
                    "Palitos de queso crujientes con salsa.",
                    new BigDecimal("7.90"),
                    entrantes
            )).setImagen("assets/tequeños.jpg");

            productoRepository.save(new Producto(
                    "Aros de cebolla 10 uds",
                    "Crujientes aros de cebolla fritos, acompañados de nuestra salsa Montana",
                    new BigDecimal("8.90"),
                    entrantes
            )).setImagen("assets/aros_cebolla.jpg");

            System.out.println("   ✓ 3 entrantes creados");

            // ===========================
            // CREAR PRODUCTOS - HAMBURGUESAS
            // ===========================
            productoRepository.save(new Producto(
                    "Hamburguesa smash con queso y bacon",
                    "180 g. de carne 100% vaca gallega, sin aditivos ni conservantes, picada a diario",
                    new BigDecimal("13.50"),
                    hamburguesas
            )).setImagen("assets/hamburguesa_smash_bacon.jpg");

            productoRepository.save(new Producto(
                    "Burger del Mes",
                    "Pan Black Brioche, 200Gr de Carne Picada de Angus Américano, 80Gr de nuestra Pulled-Pork Casero, Cebolla Caramelizada, Queso Fundido y Salsa Emmy",
                    new BigDecimal("16.50"),
                    hamburguesas
            )).setImagen("assets/Burger_del_mes.jpg");

            productoRepository.save(new Producto(
                    "Crispy chicken toro hot",
                    "Nuestra hamburguesa 100% de pollo crispy",
                    new BigDecimal("12.90"),
                    hamburguesas
            )).setImagen("assets/crispy_chicken_toro_hot.jpg");

            productoRepository.save(new Producto(
                    "Hamburguesa smash",
                    "180 g. de carne 100% vaca gallega, sin aditivos ni conservantes, picada a diario",
                    new BigDecimal("13.50"),
                    hamburguesas
            )).setImagen("assets/hamburguesa_smash.jpg");

            System.out.println("   ✓ 4 hamburguesas creadas");

            // ===========================
            // CREAR PRODUCTOS - GUARNICIONES
            // ===========================
            productoRepository.save(new Producto(
                    "Batatas Cajun",
                    "batatas fritas cubiertas de nuestra mezcla de especias Cajun (contiene Sal).",
                    new BigDecimal("4.90"),
                    guarniciones
            )).setImagen("assets/batatas_cajun.jpg");

            productoRepository.save(new Producto(
                    "Batata frita",
                    "Boniato frito.",
                    new BigDecimal("4.90"),
                    guarniciones
            )).setImagen("assets/batata_frita.jpg");

            productoRepository.save(new Producto(
                    "Papas cajún",
                    "Papas fritas sazonadas con un mix de especias (la mezcla de especias contiene sal).",
                    new BigDecimal("3.50"),
                    guarniciones
            )).setImagen("assets/papas_cajun.jpg");

            productoRepository.save(new Producto(
                    "Papas clásicas",
                    "Patatas fritas",
                    new BigDecimal("3.60"),
                    guarniciones
            )).setImagen("assets/papas_clasicas.jpg");

            System.out.println("   ✓ 4 guarniciones creadas");

            // ===========================
            // CREAR PRODUCTOS - POSTRES
            // ===========================
            productoRepository.save(new Producto(
                    "Chesscake de Pistacho Casera",
                    "Deliciosa Cheesecake Casera con Topping de Pistacho.",
                    new BigDecimal("6.50"),
                    postres
            )).setImagen("assets/cheescake_pistacho.jpg");

            productoRepository.save(new Producto(
                    "Cheesecake de galletas Lotus casera",
                    "Deliciosa tarta casera de queso y galletas Oreo.",
                    new BigDecimal("5.85"),
                    postres
            )).setImagen("assets/cheescake_lotus.jpg");

            productoRepository.save(new Producto(
                    "Cheesecake casera",
                    "Deliciosa tarta de queso casera",
                    new BigDecimal("6.50"),
                    postres
            )).setImagen("assets/cheescake.jpg");

            productoRepository.save(new Producto(
                    "Brownie De Chocolate",
                    "Con helado de Vainilla y salsa de chocolate.",
                    new BigDecimal("5.50"),
                    postres
            )).setImagen("assets/brownie.jpg");

            System.out.println("   ✓ 4 postres creados");

            // ===========================
            // CREAR PRODUCTOS - BEBIDAS
            // ===========================
            productoRepository.save(new Producto(
                    "Nestea Té Negro Limón lata 330ml.",
                    "",
                    new BigDecimal("2.50"),
                    bebidas
            )).setImagen("assets/nestea_te_negro.jpg");

            productoRepository.save(new Producto(
                    "Appletiser Botella",
                    "",
                    new BigDecimal("2.90"),
                    bebidas
            )).setImagen("assets/appletiser.jpg");

            productoRepository.save(new Producto(
                    "Agua Con Gas (1.50 Lt.)",
                    "",
                    new BigDecimal("3.00"),
                    bebidas
            )).setImagen("assets/botella_agua_1.50.jpg");

            productoRepository.save(new Producto(
                    "Agua Con Gas (50 Cl.)",
                    "",
                    new BigDecimal("1.50"),
                    bebidas
            )).setImagen("assets/botella_agua_0.50.jpg");

            System.out.println("   ✓ 4 bebidas creadas");

            // ===========================
            // RESUMEN
            // ===========================
            long totalCategorias = categoriaRepository.count();
            long totalProductos = productoRepository.count();

            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("✅ Base de datos inicializada correctamente");
            System.out.println("   📁 Categorías: " + totalCategorias);
            System.out.println("   🍔 Productos: " + totalProductos);
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        };
    }
}
