package com.cactech.api.config;

import com.cactech.api.model.Departamento;
import com.cactech.api.model.Produto;
import com.cactech.api.repository.ProdutoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

import static com.cactech.api.model.Departamento.COMPUTADORES;
import static com.cactech.api.model.Departamento.HARDWARE;
import static com.cactech.api.model.Departamento.KIT_UPGRADE;
import static com.cactech.api.model.Departamento.MONITORES;

/**
 * Cadastra o catalogo inicial da loja na primeira execucao (somente se a tabela estiver vazia).
 */
@Component
public class CatalogoSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(CatalogoSeeder.class);
    private static final String IMG = "assets/products/";

    private final ProdutoRepository produtoRepository;

    public CatalogoSeeder(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (produtoRepository.count() > 0) {
            return;
        }
        List<Produto> catalogo = catalogoInicial();
        produtoRepository.saveAll(catalogo);
        log.info("Catalogo inicial cadastrado: {} produtos", catalogo.size());
    }

    static List<Produto> catalogoInicial() {
        return List.of(
                p("Placa mãe AsRock X870 Pro", "1760.72", "ASROCK_PLACAMAE_X870_PRO.jpg", HARDWARE, "Placa-mãe"),
                p("Placa mãe GIGABYTE A520M", "444.00", "PLACAMAE_GIGABYTE_A520M.jpg", HARDWARE, "Placa-mãe"),
                p("Placa mãe MSI A520-A Pro", "443.64", "PLACAMAE_MSI_A520-A_PRO.jpg", HARDWARE, "Placa-mãe"),
                p("Memória XPG Spectrix RGB 16GB DDR4 3200Mhz", "447.90", "MEMÓRIA_RAM_XPG_SPECTRIX_RGB_16GB_DDR4_3200MHZ.jpg", HARDWARE, "Memória RAM"),
                p("Memória Corsair VENGEANCE RGB 32GB DDR5", "810.00", "CORSAIR_VENGEANCE_RGB_DDR5_RAM_32GB.jpg", HARDWARE, "Memória RAM"),
                p("Memória FURY Beast 16GB DDR4 3200Mhz", "469.99", "MEMÓRIA_RAM_16GB_DDR4_3200MHZ_FURY_BEAST.jpg", HARDWARE, "Memória RAM"),
                p("Fonte de alimentação Corsair atx 650W 80 Plus Bronze", "455.99", "FONTE_CORSAIR_ATX_650W_80_PLUSBRONZE.jpg", HARDWARE, "Fonte"),
                p("Fonte de alimentação Maximus atx 650W 80 Plus Bronze", "319.99", "FONTE_MAXIMUS_ATX_650W_80_PLUSBRONZE.jpg", HARDWARE, "Fonte"),
                p("Fonte de alimentação MSI atx 600W 80 Plus", "499.00", "FONTE_MSI_MAG_600W_80_PLUS.jpg", HARDWARE, "Fonte"),
                p("Processador Intel CORE I5 14400F", "2600.50", "PROCESSADOR_INTEL_COREI5_14400F.jpg", HARDWARE, "Processador"),
                p("Processador AMD Ryzen 5 5600GT", "899.00", "PROCESSADOR_AMD_5_5600GT.jpg", HARDWARE, "Processador"),
                p("Placa de vídeo RTX 3050 6GB GALAX", "1460.00", "PLACADEVIDEO_RTX3050_6GB_GALAX.jpg", HARDWARE, "Placa de vídeo"),
                p("Placa de vídeo RTX 5060 8GB MSI", "2151.00", "PLACADEVIDEO_RTX5060_8GB_MSI.jpg", HARDWARE, "Placa de vídeo"),
                p("Placa de vídeo RX580 8GB AMD", "799.99", "PLACADEVIDEO_RX580_8GB_AMD.jpg", HARDWARE, "Placa de vídeo"),
                p("Monitor Gamer LG UltraGear, 27 Pol, OLED, QHD, 0.03ms, 240Hz, G-Sync/FreeSync Premium Pro, HDMI/DP", "5499.99", "MonitorGamerLGUltraGear,27Pol.jpg", MONITORES, "Monitor"),
                p("Monitor Gamer AOC Agon CS2 25, 24.5 Pol, IPS, FHD, 0.3ms, 310Hz, G-Sync, HDMI/DP, CS25G", "1899.99", "MonitorGamerAOCAgon.jpg", MONITORES, "Monitor"),
                p("Monitor Gamer Samsung Odyssey G9, 49 Pol, VA, DQHD, Curvo, 1ms, 144Hz, FreeSync Premium Pro, HDMI/DP", "7249.99", "MonitorGamerSamsungOdysseyG9.jpg", MONITORES, "Monitor"),
                p("Monitor Gamer Cooler Master GA22FC, 21.45 Pol, VA, FHD, 1ms, 100Hz, Adaptive Sync, HDMI/VGA", "449.99", "MonitorGamerCoolerMasterGA22FC.jpg", MONITORES, "Monitor"),
                p("Monitor Gamer Pichau Cepheus V4S, 32 Pol, VA, 2K, 1ms, 165Hz, HDMI/DP", "1099.99", "MonitorGamerPichauCepheusV4S.jpg", MONITORES, "Monitor"),
                p("Monitor Gamer Mancer Valak ZX181, 27 Pol, VA, Curvo, FHD, 1ms, 180Hz, FreeSync, HDMI/DP", "789.99", "MonitorGamerMancerValakZX181.jpg", MONITORES, "Monitor"),
                p("Monitor Gamer AOC 27G2S, 27 Pol, VA, FHD, 1ms, 165Hz, FreeSync Premium, HDMI/DP", "899.99", "MonitorGamerAOC27G2S.jpg", MONITORES, "Monitor"),
                p("Pichau Kit upgrade, AMD Ryzen 5 5500X3D, A520M DDR4", "1289.18", "KIT_UPGRADE_AMD_5500_A520M.jpg", KIT_UPGRADE, "Kit Upgrade"),
                p("Pichau Kit Upgrade, Intel Core Ultra 7 265KF, B860M DDR5, 16GB", "3799.99", "PichauKitUpgradeIntelCoreUltra7265KFB860MDDR516GB.jpg", KIT_UPGRADE, "Kit Upgrade"),
                p("Pichau Kit upgrade, Intel i7-12700KF, B760M DDR4, 16GB", "2269.99", "PichauKitupgradeInteli7-12700KFB760MDDR416GB.jpg", KIT_UPGRADE, "Kit Upgrade"),
                p("PC Pichau Lechies, Intel i5-8400, UHD Intel 630, 8GB DDR4, SSD 240GB", "1213.01", "PCPichauLechiesInteli5-8400UHDIntel6308GBDDR4,SSD240GB.jpg", COMPUTADORES, "Computador"),
                p("PC Gamer Pichau Crisipo IV, AMD Ryzen 5 5600X, Radeon RX 9060 8GB, 16GB DDR4, SSD M.2 1TB", "4490.82", "PCGamerPichauCrisipoIVAMDRyzen55600X,RadeonRX90608GB,16GBDDR4,SSDM.21TB.jpg", COMPUTADORES, "Computador"),
                p("PC Gamer Pichau Luwu III, Intel i7-12700K, GeForce RTX 4070 Super 12GB, 16GB DDR5, SSD M.2 1TB", "9259.49", "PCGamerPichauLuwuIIIInteli7-12700KGeForceRTX4070Super12GB16GBDDR5SSDM.21TB.jpg", COMPUTADORES, "Computador"),
                p("PC Pichau Gamer Afrodite, AMD Ryzen 7 5700X, GeForce RTX 5060 8GB, 16GB DDR4, SSD M.2 1TB", "5199.99", "PCPichauGamerAfroditeAMDRyzen75700XGeForceRTX50608GB16GBDDR4SSDM.21TB.jpg", COMPUTADORES, "Computador")
        );
    }

    private static Produto p(String nome, String preco, String arquivo, Departamento departamento, String categoria) {
        return new Produto(nome, new BigDecimal(preco), IMG + arquivo, departamento, categoria);
    }
}
