package com.infnet.infnetPB;

import com.infnet.infnetPB.controller.MesaControllerTest;
import com.infnet.infnetPB.controller.PedidoControllerTest;
import com.infnet.infnetPB.controller.ReservaControllerTest;
import com.infnet.infnetPB.controller.RestauranteControllerTest;
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
		mesaControllerTest.testCreateMesaFailure();
		mesaControllerTest.testGetMesaByIdFailure();
		mesaControllerTest.testDeleteMesaFailure();
		mesaControllerTest.testUpdateMesaFailure();
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
		pedidoControllerTest.testCreatePedidoFailure();
		pedidoControllerTest.testDeletePedidoByIdFailure();
		pedidoControllerTest.testUpdatePedidoFailure();
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
		reservaControllerTest.testGetReservaById();
		reservaControllerTest.testCreateReservaFailure();
		reservaControllerTest.testDeleteReservaByIdFailure();
		reservaControllerTest.testGetReservaByIdNotFound();
		reservaControllerTest.testUpdateReservaFailure();
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
		restauranteControllerTest.testCreateRestaurante_Failure();
		restauranteControllerTest.testUpdateRestaurante_Failure();
		restauranteControllerTest.testDeleteRestauranteById_Failure();
	}
}