package com.wishdish.config;

import com.wishdish.models.*;
import com.wishdish.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(
            CategoryRepository categoryRepository,
            ProductRepository productRepository,
            DiningTableRepository diningTableRepository,
            IngredientRepository ingredientRepository,
            ProductIngredientRepository productIngredientRepository,
            UserRepository userRepository) {

        return args -> {
            System.out.println("🔄 Verificando datos en la base de datos...");

            // ===========================
            // CREAR USUARIOS (STAFF)
            // ===========================
            if (userRepository.count() == 0) {
                System.out.println("   👥 Creando usuarios del sistema...");
                crearUsuario(userRepository, "Tomas", User.Role.ADMIN, "admin");
                crearUsuario(userRepository, "Sol", User.Role.KITCHEN, "kitchen");
                crearUsuario(userRepository, "Carlos", User.Role.KITCHEN, "carlos");
                crearUsuario(userRepository, "Crisa", User.Role.WAITER, "waiter");
                System.out.println("   ✓ 4 usuarios creados con contraseñas encriptadas");
            } else {
                System.out.println("   ✅ Usuarios ya existen (" + userRepository.count() + " usuarios)");
            }


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
                imprimirResumen(categoryRepository, productRepository, diningTableRepository, userRepository);
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
            Product nachosConQueso = crearProducto(productRepository, "Nachos con Queso", "Nachos crujientes con mezcla de quesos y guacamole.", "8.90", "assets/nachos.jpg", entrantes);
            Product tequeños = crearProducto(productRepository, "Tequeños", "Palitos de queso crujientes con salsa.", "7.90", "assets/tequeños.jpg", entrantes);
            Product arosDeCebolla = crearProducto(productRepository, "Aros de cebolla 10 uds", "Crujientes aros de cebolla fritos, acompañados de nuestra salsa Montana", "8.90", "assets/aros_cebolla.jpg", entrantes);
            System.out.println("   ✓ 3 entrantes creados");

            // ===========================
            // CREAR PRODUCTOS - HAMBURGUESAS
            // ===========================
            Product hamburguesaBacon = crearProducto(productRepository, "Hamburguesa smash con queso y bacon", "180 g. de carne 100% vaca gallega, sin aditivos ni conservantes, picada a diario", "13.50", "assets/hamburguesa_smash_bacon.jpg", hamburguesas);
            Product burgerDelMes = crearProducto(productRepository, "Burger del Mes", "Pan Black Brioche, 200Gr de Carne Picada de Angus Américano, 80Gr de nuestra Pulled-Pork Casero, Cebolla Caramelizada, Queso Fundido y Salsa Emmy", "16.50", "assets/Burger_del_mes.jpg", hamburguesas);
            Product crispyChickenToroHot = crearProducto(productRepository, "Crispy chicken toro hot", "Nuestra hamburguesa 100% de pollo crispy", "12.90", "assets/crispy_chicken_toro_hot.jpg", hamburguesas);
            Product hamburguesaSmash = crearProducto(productRepository, "Hamburguesa smash", "180 g. de carne 100% vaca gallega, sin aditivos ni conservantes, picada a diario", "13.50", "assets/hamburguesa_smash.jpg", hamburguesas);
            System.out.println("   ✓ 4 hamburguesas creadas");

            // ===========================
            // CREAR PRODUCTOS - GUARNICIONES
            // ===========================

            // Nota: para poder unir los productos con ingredientes, los guardamos en una variable
            Product batataCajun = crearProducto(productRepository, "Batatas Cajun", "batatas fritas cubiertas de nuestra mezcla de especias Cajun (contiene Sal).", "4.90", "assets/batatas_cajun.jpg", guarniciones);
            Product batataFrita = crearProducto(productRepository, "Batata frita", "Boniato frito.", "4.90", "assets/batata_frita.jpg", guarniciones);
            Product papasCajun = crearProducto(productRepository, "Papas cajún", "Papas fritas sazonadas con un mix de especias (la mezcla de especias contiene sal).", "3.50", "assets/papas_cajun.jpg", guarniciones);
            Product papasClasicas = crearProducto(productRepository, "Papas clásicas", "Patatas fritas", "3.60", "assets/papas_clasicas.jpg", guarniciones);
            System.out.println("   ✓ 4 guarniciones creadas");

            // ===========================
            // CREAR PRODUCTOS - POSTRES
            // ===========================
            Product cheesecakeDePistachoCasera = crearProducto(productRepository, "Cheesecake de Pistacho Casera", "Deliciosa Cheesecake Casera con Topping de Pistacho.", "6.50", "assets/cheescake_pistacho.jpg", postres);
            Product cheesecakeDeGalletasLotusCasera = crearProducto(productRepository, "Cheesecake de galletas Lotus casera", "Deliciosa tarta casera de queso y galletas Oreo.", "5.85", "assets/cheescake_lotus.jpg", postres);
            Product cheescakeCasera = crearProducto(productRepository, "Cheesecake casera", "Deliciosa tarta de queso casera", "6.50", "assets/cheescake.jpg", postres);
            Product brownieDeChocolate = crearProducto(productRepository, "Brownie De Chocolate", "Con helado de Vainilla y salsa de chocolate.", "5.50", "assets/brownie.jpg", postres);
            System.out.println("   ✓ 4 postres creados");

            // ===========================
            // CREAR PRODUCTOS - BEBIDAS
            // ===========================
            Product nesteaNegro = crearProducto(productRepository, "Nestea Té Negro Limón lata 330ml.", "", "2.50", "assets/nestea_te_negro.jpg", bebidas);
            Product appletiserBotella = crearProducto(productRepository, "Appletiser Botella", "", "2.90", "assets/appletiser.jpg", bebidas);
            Product aguaConGas = crearProducto(productRepository, "Agua Con Gas (1.50 Lt.)", "", "3.00", "assets/botella_agua_1.50.jpg", bebidas);
            Product aguaConGaspequeña = crearProducto(productRepository, "Agua Con Gas (50 Cl.)", "", "1.50", "assets/botella_agua_0.50.jpg", bebidas);
            System.out.println("   ✓ 4 bebidas creadas");


            // ================================
            // CREAR INGREDIENTES CON PRECIOS
            // ================================
            System.out.println("   🍅 Creando ingredientes...");

            // Carnes y Proteínas
            Ingredient carneVaca = crearIngrediente(ingredientRepository, "Carne 100% vaca gallega", "3.00");
            Ingredient carneAngus = crearIngrediente(ingredientRepository, "Carne picada de Angus", "3.50");
            Ingredient polloCrispy = crearIngrediente(ingredientRepository, "Pollo Crispy", "2.50");
            Ingredient pulledPork = crearIngrediente(ingredientRepository, "Pulled Pork casero", "2.50");
            Ingredient bacon = crearIngrediente(ingredientRepository, "Bacon crujiente", "1.50");
            Ingredient huevoFrito = crearIngrediente(ingredientRepository, "Huevo frito", "1.00");

            // Quesos
            Ingredient mezclaQuesos = crearIngrediente(ingredientRepository, "Mezcla de quesos", "1.20");
            Ingredient quesoCheddar = crearIngrediente(ingredientRepository, "Queso Cheddar fundido", "1.00");
            Ingredient quesoBlanco = crearIngrediente(ingredientRepository, "Queso blanco hilado", "1.50");

            // Vegetales y Salsas
            Ingredient guacamole = crearIngrediente(ingredientRepository, "Guacamole", "1.50");
            Ingredient jalapenos = crearIngrediente(ingredientRepository, "Jalapeños", "0.80");
            Ingredient cebollaCaramelizada = crearIngrediente(ingredientRepository, "Cebolla Caramelizada", "0.80");
            Ingredient cebollaMorada = crearIngrediente(ingredientRepository, "Cebolla Morada", "0.50");
            Ingredient salsaMontana = crearIngrediente(ingredientRepository, "Salsa Montana", "0.80");
            Ingredient salsaEmmy = crearIngrediente(ingredientRepository, "Salsa Emmy", "1.00");
            Ingredient salsaFrutosRojos = crearIngrediente(ingredientRepository, "Salsa de frutos rojos", "0.80");

            // Panes y Bases
            Ingredient panBrioche = crearIngrediente(ingredientRepository, "Pan Brioche", "1.00");
            Ingredient panBlackBrioche = crearIngrediente(ingredientRepository, "Pan Black Brioche", "1.20");
            Ingredient totopos = crearIngrediente(ingredientRepository, "Nachos (Totopos)", "1.50");
            Ingredient especiasCajun = crearIngrediente(ingredientRepository, "Mix especias Cajún", "0.50");

            // Postres y Bebidas
            Ingredient toppingPistacho = crearIngrediente(ingredientRepository, "Topping de Pistacho", "1.00");
            Ingredient galletaLotus = crearIngrediente(ingredientRepository, "Polvo de galleta Lotus", "0.80");
            Ingredient heladoVainilla = crearIngrediente(ingredientRepository, "Bola de helado de Vainilla", "1.50");
            Ingredient siropeChocolate = crearIngrediente(ingredientRepository, "Salsa de chocolate", "0.50");
            Ingredient hielo = crearIngrediente(ingredientRepository, "Hielo", "0.00");
            Ingredient rodajaLimon = crearIngrediente(ingredientRepository, "Rodaja de limón", "0.00");

            // ================================
            // 2. UNIR INGREDIENTES CON PLATOS
            // ================================

            // Como al crear los productos los guardamos en una variable, unimos los productos con los ingredientes
            // (true = Ingrediente Por Defecto, false = Ingrediente Extra)
            System.out.println("   Conectando ingredientes a la carta...");

            // --- ENTRANTES ---
            // Nachos
            aniadirIngredienteAPlato(productIngredientRepository, nachosConQueso, totopos, true);
            aniadirIngredienteAPlato(productIngredientRepository, nachosConQueso, mezclaQuesos, true);
            aniadirIngredienteAPlato(productIngredientRepository, nachosConQueso, guacamole, true);
            aniadirIngredienteAPlato(productIngredientRepository, nachosConQueso, jalapenos, false);
            aniadirIngredienteAPlato(productIngredientRepository, nachosConQueso, pulledPork, false);

            // Tequeños
            aniadirIngredienteAPlato(productIngredientRepository, tequeños, quesoBlanco, true);
            aniadirIngredienteAPlato(productIngredientRepository, tequeños, salsaFrutosRojos, true);
            aniadirIngredienteAPlato(productIngredientRepository, tequeños, salsaMontana, false);

            // Aros de cebolla
            aniadirIngredienteAPlato(productIngredientRepository, arosDeCebolla, salsaMontana, true);
            aniadirIngredienteAPlato(productIngredientRepository, arosDeCebolla, bacon, false);


            // --- HAMBURGUESAS ---
            // Smash Bacon
            aniadirIngredienteAPlato(productIngredientRepository, hamburguesaBacon, panBrioche, true);
            aniadirIngredienteAPlato(productIngredientRepository, hamburguesaBacon, carneVaca, true);
            aniadirIngredienteAPlato(productIngredientRepository, hamburguesaBacon, quesoCheddar, true);
            aniadirIngredienteAPlato(productIngredientRepository, hamburguesaBacon, bacon, true);
            aniadirIngredienteAPlato(productIngredientRepository, hamburguesaBacon, huevoFrito, false);
            aniadirIngredienteAPlato(productIngredientRepository, hamburguesaBacon, cebollaCaramelizada, false);

            // Burger del Mes
            aniadirIngredienteAPlato(productIngredientRepository, burgerDelMes, panBlackBrioche, true);
            aniadirIngredienteAPlato(productIngredientRepository, burgerDelMes, carneAngus, true);
            aniadirIngredienteAPlato(productIngredientRepository, burgerDelMes, pulledPork, true);
            aniadirIngredienteAPlato(productIngredientRepository, burgerDelMes, cebollaCaramelizada, true);
            aniadirIngredienteAPlato(productIngredientRepository, burgerDelMes, mezclaQuesos, true);
            aniadirIngredienteAPlato(productIngredientRepository, burgerDelMes, salsaEmmy, true);
            aniadirIngredienteAPlato(productIngredientRepository, burgerDelMes, bacon, false);
            aniadirIngredienteAPlato(productIngredientRepository, burgerDelMes, huevoFrito, false);

            // Crispy Chicken
            aniadirIngredienteAPlato(productIngredientRepository, crispyChickenToroHot, panBrioche, true);
            aniadirIngredienteAPlato(productIngredientRepository, crispyChickenToroHot, polloCrispy, true);
            aniadirIngredienteAPlato(productIngredientRepository, crispyChickenToroHot, quesoCheddar, true);
            aniadirIngredienteAPlato(productIngredientRepository, crispyChickenToroHot, bacon, false);
            aniadirIngredienteAPlato(productIngredientRepository, crispyChickenToroHot, guacamole, false);

            // Smash Normal
            aniadirIngredienteAPlato(productIngredientRepository, hamburguesaSmash, panBrioche, true);
            aniadirIngredienteAPlato(productIngredientRepository, hamburguesaSmash, carneVaca, true);
            aniadirIngredienteAPlato(productIngredientRepository, hamburguesaSmash, quesoCheddar, true);
            aniadirIngredienteAPlato(productIngredientRepository, hamburguesaSmash, bacon, false);
            aniadirIngredienteAPlato(productIngredientRepository, hamburguesaSmash, cebollaMorada, false);


            // --- GUARNICIONES ---
            // Batatas y Papas Cajun
            aniadirIngredienteAPlato(productIngredientRepository, batataCajun, especiasCajun, true);
            aniadirIngredienteAPlato(productIngredientRepository, papasCajun, especiasCajun, true);

            // Opciones extra para todas las guarniciones
            aniadirIngredienteAPlato(productIngredientRepository, batataCajun, salsaMontana, false);
            aniadirIngredienteAPlato(productIngredientRepository, batataFrita, salsaMontana, false);
            aniadirIngredienteAPlato(productIngredientRepository, papasCajun, salsaMontana, false);
            aniadirIngredienteAPlato(productIngredientRepository, papasClasicas, salsaMontana, false);
            aniadirIngredienteAPlato(productIngredientRepository, papasClasicas, mezclaQuesos, false);
            aniadirIngredienteAPlato(productIngredientRepository, papasClasicas, bacon, false);


            // --- POSTRES ---
            aniadirIngredienteAPlato(productIngredientRepository, cheesecakeDePistachoCasera, toppingPistacho, true);
            aniadirIngredienteAPlato(productIngredientRepository, cheesecakeDePistachoCasera, heladoVainilla, false);

            aniadirIngredienteAPlato(productIngredientRepository, cheesecakeDeGalletasLotusCasera, galletaLotus, true);
            aniadirIngredienteAPlato(productIngredientRepository, cheesecakeDeGalletasLotusCasera, siropeChocolate, false);

            aniadirIngredienteAPlato(productIngredientRepository, brownieDeChocolate, heladoVainilla, true);
            aniadirIngredienteAPlato(productIngredientRepository, brownieDeChocolate, siropeChocolate, true);
            aniadirIngredienteAPlato(productIngredientRepository, brownieDeChocolate, toppingPistacho, false);


            // --- BEBIDAS  ---
            aniadirIngredienteAPlato(productIngredientRepository, nesteaNegro, hielo, true);
            aniadirIngredienteAPlato(productIngredientRepository, nesteaNegro, rodajaLimon, true);

            aniadirIngredienteAPlato(productIngredientRepository, aguaConGas, hielo, false);
            aniadirIngredienteAPlato(productIngredientRepository, aguaConGas, rodajaLimon, false);

            aniadirIngredienteAPlato(productIngredientRepository, aguaConGaspequeña, hielo, false);
            aniadirIngredienteAPlato(productIngredientRepository, aguaConGaspequeña, rodajaLimon, false);

            System.out.println("   ✓ Todos los ingredientes conectados");


            imprimirResumen(categoryRepository, productRepository, diningTableRepository, userRepository);
        };
    }

    // --- NUEVO: Métod para crear usuarios con contraseñas encriptadas ---
    private void crearUsuario(UserRepository repo, String name, User.Role role, String rawPassword) {
        User user = new User();
        user.setName(name);
        user.setRole(role);
        user.setPinHash(hashPassword(rawPassword)); // Encriptamos antes de guardar
        repo.save(user);
    }

    // --- NUEVO: Métod de encriptación SHA-256 ---
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al encriptar la contraseña", e);
        }
    }

    // Método auxiliar para no repetir código creando productos
    private Product crearProducto(ProductRepository repo, String nombre, String desc, String precio, String imagen, Category categoria) {
        Product p = new Product();
        p.setName(nombre);
        p.setDescription(desc);
        p.setPrice(new BigDecimal(precio));
        p.setPicture(imagen);
        p.setCategory(categoria);
        return repo.save(p);
    }

    private Ingredient crearIngrediente(IngredientRepository repo, String nombre, String precioExtra) {
        Ingredient ing = new Ingredient(nombre);
        ing.setExtraPrice(new BigDecimal(precioExtra));
        return repo.save(ing);
    }

    // Para unir plato con el ingrediente
    private void aniadirIngredienteAPlato(ProductIngredientRepository repo, Product producto, Ingredient ingrediente, boolean porDefecto) {
        ProductIngredient relacion = new ProductIngredient(producto, ingrediente, porDefecto);
        repo.save(relacion);
    }

    // Método auxiliar para el resumen final
    private void imprimirResumen(CategoryRepository catRepo, ProductRepository prodRepo, DiningTableRepository mesaRepo, UserRepository userRepo) {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("✅ Base de datos inicializada correctamente");
        System.out.println("   👥 Usuarios: " + userRepo.count());
        System.out.println("   📁 Categorías: " + catRepo.count());
        System.out.println("   🍔 Productos: " + prodRepo.count());
        System.out.println("   🪑 Mesas: " + mesaRepo.count());
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
}