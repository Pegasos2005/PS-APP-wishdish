package com.wishdish.config;

import com.wishdish.models.Category;
import com.wishdish.models.DiningTable;
import com.wishdish.models.Product;
import com.wishdish.repositories.CategoryRepository;
import com.wishdish.repositories.DiningTableRepository;
import com.wishdish.repositories.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(
            CategoryRepository categoryRepository,
            ProductRepository productRepository,
            DiningTableRepository diningTableRepository) {

        return args -> {
            System.out.println("🔄 Verificando datos en la base de datos...");

            // ===========================
            // CREAR MESAS (verificación independiente)
            // ===========================
            if (diningTableRepository.count() == 0) {
                System.out.println("   🪑 Creando mesas...");
                for (int i = 1; i <= 10; i++) {
                    DiningTable table = new DiningTable();
                    table.setTableNumber(i);
                    diningTableRepository.save(table);
                }
                System.out.println("   ✓ 10 mesas creadas");
            } else {
                System.out.println("   ✅ Mesas ya existen (" + diningTableRepository.count() + " mesas)");
            }

            // ===========================
            // VERIFICAR PRODUCTOS Y CATEGORÍAS
            // ===========================
            if (categoryRepository.count() > 0) {
                System.out.println("   ✅ Categorías y productos ya existen. Omitiendo carga de productos.");
                imprimirResumen(categoryRepository, productRepository, diningTableRepository);
                return;
            }

            System.out.println("   📦 Cargando categorías y productos...");

            // ===========================
            // CREAR CATEGORÍAS
            // ===========================
            Category entrantes = new Category("Entrantes");
            entrantes.setDescription("Platos ligeros para comenzar tu comida");
            entrantes = categoryRepository.save(entrantes);

            Category hamburguesas = new Category("Hamburguesas");
            hamburguesas.setDescription("Nuestras hamburguesas gourmet hechas con carne 100% de vaca gallega");
            hamburguesas = categoryRepository.save(hamburguesas);

            Category guarniciones = new Category("Guarniciones");
            guarniciones.setDescription("Acompañamientos perfectos para tu plato principal");
            guarniciones = categoryRepository.save(guarniciones);

            Category postres = new Category("Postres");
            postres.setDescription("Dulces caseros para terminar con un toque especial");
            postres = categoryRepository.save(postres);

            Category bebidas = new Category("Bebidas");
            bebidas.setDescription("Refrescos, aguas y bebidas para acompañar tu comida");
            bebidas = categoryRepository.save(bebidas);

            System.out.println("   ✓ 5 categorías creadas");

            // ===========================
            // CREAR PRODUCTOS - ENTRANTES
            // ===========================
            crearProducto(productRepository, "Nachos con Queso", "Nachos crujientes con mezcla de quesos y guacamole.", "8.90", "assets/nachos.jpg", entrantes);
            crearProducto(productRepository, "Tequeños", "Palitos de queso crujientes con salsa.", "7.90", "assets/tequeños.jpg", entrantes);
            crearProducto(productRepository, "Aros de cebolla 10 uds", "Crujientes aros de cebolla fritos, acompañados de nuestra salsa Montana", "8.90", "assets/aros_cebolla.jpg", entrantes);
            System.out.println("   ✓ 3 entrantes creados");

            // ===========================
            // CREAR PRODUCTOS - HAMBURGUESAS
            // ===========================
            crearProducto(productRepository, "Hamburguesa smash con queso y bacon", "180 g. de carne 100% vaca gallega, sin aditivos ni conservantes, picada a diario", "13.50", "assets/hamburguesa_smash_bacon.jpg", hamburguesas);
            crearProducto(productRepository, "Burger del Mes", "Pan Black Brioche, 200Gr de Carne Picada de Angus Américano, 80Gr de nuestra Pulled-Pork Casero, Cebolla Caramelizada, Queso Fundido y Salsa Emmy", "16.50", "assets/Burger_del_mes.jpg", hamburguesas);
            crearProducto(productRepository, "Crispy chicken toro hot", "Nuestra hamburguesa 100% de pollo crispy", "12.90", "assets/crispy_chicken_toro_hot.jpg", hamburguesas);
            crearProducto(productRepository, "Hamburguesa smash", "180 g. de carne 100% vaca gallega, sin aditivos ni conservantes, picada a diario", "13.50", "assets/hamburguesa_smash.jpg", hamburguesas);
            System.out.println("   ✓ 4 hamburguesas creadas");

            // ===========================
            // CREAR PRODUCTOS - GUARNICIONES
            // ===========================
            crearProducto(productRepository, "Batatas Cajun", "batatas fritas cubiertas de nuestra mezcla de especias Cajun (contiene Sal).", "4.90", "assets/batatas_cajun.jpg", guarniciones);
            crearProducto(productRepository, "Batata frita", "Boniato frito.", "4.90", "assets/batata_frita.jpg", guarniciones);
            crearProducto(productRepository, "Papas cajún", "Papas fritas sazonadas con un mix de especias (la mezcla de especias contiene sal).", "3.50", "assets/papas_cajun.jpg", guarniciones);
            crearProducto(productRepository, "Papas clásicas", "Patatas fritas", "3.60", "assets/papas_clasicas.jpg", guarniciones);
            System.out.println("   ✓ 4 guarniciones creadas");

            // ===========================
            // CREAR PRODUCTOS - POSTRES
            // ===========================
            crearProducto(productRepository, "Chesscake de Pistacho Casera", "Deliciosa Cheesecake Casera con Topping de Pistacho.", "6.50", "assets/cheescake_pistacho.jpg", postres);
            crearProducto(productRepository, "Cheesecake de galletas Lotus casera", "Deliciosa tarta casera de queso y galletas Oreo.", "5.85", "assets/cheescake_lotus.jpg", postres);
            crearProducto(productRepository, "Cheesecake casera", "Deliciosa tarta de queso casera", "6.50", "assets/cheescake.jpg", postres);
            crearProducto(productRepository, "Brownie De Chocolate", "Con helado de Vainilla y salsa de chocolate.", "5.50", "assets/brownie.jpg", postres);
            System.out.println("   ✓ 4 postres creados");

            // ===========================
            // CREAR PRODUCTOS - BEBIDAS
            // ===========================
            crearProducto(productRepository, "Nestea Té Negro Limón lata 330ml.", "", "2.50", "assets/nestea_te_negro.jpg", bebidas);
            crearProducto(productRepository, "Appletiser Botella", "", "2.90", "assets/appletiser.jpg", bebidas);
            crearProducto(productRepository, "Agua Con Gas (1.50 Lt.)", "", "3.00", "assets/botella_agua_1.50.jpg", bebidas);
            crearProducto(productRepository, "Agua Con Gas (50 Cl.)", "", "1.50", "assets/botella_agua_0.50.jpg", bebidas);
            System.out.println("   ✓ 4 bebidas creadas");

            imprimirResumen(categoryRepository, productRepository, diningTableRepository);
        };
    }

    // Método auxiliar para no repetir código creando productos
    private void crearProducto(ProductRepository repo, String nombre, String desc, String precio, String imagen, Category categoria) {
        Product p = new Product();
        p.setName(nombre);
        p.setDescription(desc);
        p.setPrice(new BigDecimal(precio));
        p.setPicture(imagen);
        p.setCategory(categoria);
        repo.save(p);
    }

    // Método auxiliar para el resumen final
    private void imprimirResumen(CategoryRepository catRepo, ProductRepository prodRepo, DiningTableRepository mesaRepo) {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("✅ Base de datos inicializada correctamente");
        System.out.println("   📁 Categorías: " + catRepo.count());
        System.out.println("   🍔 Productos: " + prodRepo.count());
        System.out.println("   🪑 Mesas: " + mesaRepo.count());
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
}