package com.infnet.infnetPB;

import com.infnet.infnetPB.controllerTest.MesaControllerTest;
import com.infnet.infnetPB.controllerTest.PedidoControllerTest;
import com.infnet.infnetPB.controllerTest.ReservaControllerTest;
import com.infnet.infnetPB.controllerTest.RestauranteControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class InfnetPbApplicationTests {

	@Test
	void testMesaController() throws Exception {
		MesaControllerTest mesaControllerTest = new MesaControllerTest();
		mesaControllerTest.setUp();
		mesaControllerTest.testCreateMesa();
		mesaControllerTest.testGetAllMesas();
		mesaControllerTest.testGetAllMesaHistories();
		mesaControllerTest.testDeleteMesa();
		mesaControllerTest.testUpdateMesa();
		mesaControllerTest.testGetMesaById();
	}
	@Test
	void testPedidoController() throws Exception {
		PedidoControllerTest pedidoControllerTest = new PedidoControllerTest();
		pedidoControllerTest.setUp();
		pedidoControllerTest.testCreatePedido();
		pedidoControllerTest.testGetAllPedidos();
		pedidoControllerTest.testGetAllPedidoHistory();
		pedidoControllerTest.testDeletePedidoById();
		pedidoControllerTest.testUpdatePedido();
	}

	@Test
	void testReservaController() throws Exception {
		ReservaControllerTest reservaControllerTest = new ReservaControllerTest();
		reservaControllerTest.setUp();
		reservaControllerTest.testCreateReserva();
		reservaControllerTest.testGetAllReservas();
		reservaControllerTest.testGetAllReservaHistories();
		reservaControllerTest.testDeleteReservaById();
		reservaControllerTest.testUpdateReserva();
	}

	@Test
	void testRestauranteController() throws Exception {
		RestauranteControllerTest restauranteControllerTest = new RestauranteControllerTest();
		restauranteControllerTest.setUp();
		restauranteControllerTest.testCreateRestaurante();
		restauranteControllerTest.testGetAllRestaurantes();
		restauranteControllerTest.testGetAllRestauranteHistories();
		restauranteControllerTest.testDeleteRestauranteById();
		restauranteControllerTest.testUpdateRestaurante();
	}
}