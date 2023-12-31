package com.cdsi.backend.inve.controllers;


import java.net.MalformedURLException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import com.cdsi.backend.inve.controllers.commons.ResponseRest;
import com.cdsi.backend.inve.controllers.generic.GenericController;
import com.cdsi.backend.inve.dto.StockLibroDTO;
import com.cdsi.backend.inve.models.entity.Articulo;
import com.cdsi.backend.inve.models.services.IArticuloService;
import com.cdsi.backend.inve.models.services.IArticuloStockService;


//@CrossOrigin(origins = {"*"}, methods= {RequestMethod.GET,RequestMethod.POST})
@RestController
@RequestMapping("/api/arti")
public class ArticuloController extends GenericController {
	
	@Autowired
	private IArticuloService artiServi;
	
	@Autowired
	private IArticuloStockService artiStkServi;
	
	@GetMapping("/stock")
	public ResponseEntity<ResponseRest> consultarArtiStock(@RequestParam String cia, @RequestParam String bodega, @RequestParam String tipo){		
		 try{
	            Object obj = this.artiStkServi.listaCiaAndBodegaAndTipo(cia, bodega, tipo);
	            if (obj != null){
	                return super.getOKRegistroRequest(obj);
	            }
	            return super.getBadIdRequest();
	        }catch (Exception e){
	        	System.out.println(e.getMessage());
	            return super.getBadRequest(e.getMessage());
	        }		
	}
	
	@GetMapping("/show/{cia}/{cod}")
	public ResponseEntity<?> show(@PathVariable("cia") String cia, @PathVariable("cod") String cod) {
		
		Articulo articulo = null;
		Map<String, Object> response = new HashMap<>();
		// log.info("cia : "+cia+"  ,cod : "+cod);
		try {
			articulo = artiServi.findByCiaAndCod(cia, cod);
		//	log.info("Articulo : "+articulo);
		} catch(DataAccessException e) {
			response.put("mensaje", "Error al realizar la consulta en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if(articulo == null) {
			response.put("mensaje", "El articulo con el id: ".concat(cod.concat(" no existe en la base de datos!")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<Articulo>(articulo, HttpStatus.OK);
	}
	
	//METODO QUE NOS PERMITE BUSCAR POR DESCRIPCION DE ARTICULOS
  	@GetMapping("/list/desc")
    public ResponseEntity<ResponseRest> buscarFecha(@RequestParam String cia, @RequestParam String desc){
        try{
            Object obj = this.artiServi.likeDescripArti(cia, desc);
            if (obj != null){
                return this.getOKConsultaRequest(obj);
            }
            return this.getBadIdRequest();
        }catch (Exception e){
            return super.getBadRequest(e.getMessage());
        }
    }
  	/*
  	public List<Articulo> listaArtiDesc(@PathVariable("cia") String cia, @PathVariable("desc") String desc ){
  		return artiServi.likeDescripArti(cia, desc);
  	}
	*/
	//METODO QUE ENVIA UNA PAGINACION DE ARTICULO y DESCRIPCION
  	@GetMapping("/listd/page/{cia}/{desc}/{page}")
  	//@Secured({"ROLE_ADMIN","ROLE_VENDEDOR","ROLE_USER"})
  	public Page<Object> pagiArtiPrecAndDesc(@PathVariable("cia") String cia,@PathVariable("desc") String desc, @PathVariable("page") Integer page ){
  		Pageable pageable = PageRequest.of(page, 8);
  		return artiServi.pagArtiPreStockAndDesc(pageable, cia, "1", desc);
  	}
  	
    //METODO QUE ENVIA UNA PAGINACION DE ARTICULO y CODIGO
  	@GetMapping("/listc/page/{cia}/{cod}/{page}")
  	//@Secured({"ROLE_ADMIN","ROLE_VENDEDOR","ROLE_USER"})
  	public Page<Object> pagiArtiPrecAndCodigo(@PathVariable("cia") String cia,@PathVariable("cod") String cod, @PathVariable("page") Integer page ){
  		Pageable pageable = PageRequest.of(page, 8);
  		return artiServi.pagArtiPreStockAndCodigo(pageable, cia, "1", cod);
  	}
  	
     //METODO QUE ENVIA UNA PAGINACION DE ARTICULO
  	/*@GetMapping("/list/page/{cia}/{page}")
  	//@Secured({"ROLE_ADMIN","ROLE_VENDEDOR","ROLE_USER"})
  	public Page<StockLibroDTO> pagiArticulos(@PathVariable("cia") String cia, @PathVariable("page") Integer page ){
  		Pageable pageable = PageRequest.of(page, 8);
  		return artiStkServi.pagArtiPrecStock(pageable, cia,"1");
  	} */
  	//=======================================================================================
  	
  	
  	@GetMapping("/list/{cia}/{cat}/{alm}/{pre}")
  	//@Secured({"ROLE_ADMIN","ROLE_VENDEDOR","ROLE_USER"})
  	public List<StockLibroDTO> pagiAllArtiCata(@PathVariable("cia") String cia,@PathVariable("cat") String cat,
  			@PathVariable("alm") String alm,@PathVariable("pre") String pre){
  		//Pageable pageable = PageRequest.of(page, 8);
  		return artiStkServi.pagArtiFindCatalogo(cia, cat, alm, pre);
  	}
  	@GetMapping("/list/{cia}/{cat}/{lin}/{alm}/{pre}")
  	//@Secured({"ROLE_ADMIN","ROLE_VENDEDOR","ROLE_USER"})
  	public List<StockLibroDTO> pagiAllArtiLin(@PathVariable("cia") String cia,@PathVariable("cat") String cat,
  			@PathVariable("lin") String lin,@PathVariable("alm") String alm,@PathVariable("pre") String pre){
  		//Pageable pageable = PageRequest.of(page, 8);
  		return artiStkServi.pagArtiFindLinea(cia, cat, lin, alm, pre);
  	}
  	@GetMapping("/list/{cia}/{cat}/{lin}/{sub}/{alm}/{pre}")
  	//@Secured({"ROLE_ADMIN","ROLE_VENDEDOR","ROLE_USER"})
  	public List<StockLibroDTO> pagiAllArtiSubLin(@PathVariable("cia") String cia,@PathVariable("cat") String cat,
  			@PathVariable("lin") String lin,@PathVariable("sub") String sub,@PathVariable("alm") String alm,@PathVariable("pre") String pre){
  		//Pageable pageable = PageRequest.of(page, 8);
  		return artiStkServi.pagArtiFindSubLinea(cia, cat, lin, sub, alm, pre);
  	}
  //METODO QUE ENVIA UNA PAGINACION DE ARTICULO
  	@GetMapping("/list/{cia}/{cat}/{lin}/{sub}/{fam}/{alm}/{pre}")
  	//@Secured({"ROLE_ADMIN","ROLE_VENDEDOR","ROLE_USER"})
  	public List<StockLibroDTO> pagiAllArti(@PathVariable("cia") String cia,@PathVariable("cat") String cat,
  			@PathVariable("lin") String lin,@PathVariable("sub") String sub,@PathVariable("fam") String fam,
  			@PathVariable("alm") String alm,@PathVariable("pre") String pre){
  		//Pageable pageable = PageRequest.of(page, 8);
  		return artiStkServi.pagArtiFind(cia, cat, lin, sub, fam, alm, pre);
  	}
  	
  	
  //=======================================================================================
    //METODO QUE NOS DEVUELVE EL PRECIO DEL ARTICULO
  	@GetMapping("/precio/{cia}/{arti}")
  	//@Secured({"ROLE_ADMIN","ROLE_VENDEDOR","ROLE_USER"})
  	public String precioArticulo(@PathVariable("cia") String cia, @PathVariable("arti") String arti ){
  		return artiServi.precioArticulo(cia, arti);
  	}
    //METODO QUE NOS DEVUELVE EL STOCK DEL ARTICULO
  	@GetMapping("/stkarti/{cia}/{arti}")
  	//@Secured({"ROLE_ADMIN","ROLE_VENDEDOR","ROLE_USER"})
  	public String saldoArticulo(@PathVariable("cia") String cia, @PathVariable("arti") String arti ){
  		return artiServi.saldoArticulo(cia, arti);
  	}
    //METODO QUE NOS DEVUELVE EL STOCK COMPROMETIDO DEL ARTICULO
  	@GetMapping("/comparti/{cia}/{arti}")
  	//@Secured({"ROLE_ADMIN","ROLE_VENDEDOR","ROLE_USER"})
  	public String articuloComprometido(@PathVariable("cia") String cia, @PathVariable("arti") String arti ){
  		return artiServi.saldoComprometido(cia, arti);
  	}
  	

  	// METODO QUE NOS PERMITE MOSTRAR LA IMAGEN DE LA FOTO
  	@GetMapping("/uploads/img/{nombreFoto:.+}")
  	public ResponseEntity<Resource> verFoto(@PathVariable String nombreFoto){
  		Path rutaFoto = Paths.get("uploads").resolve(nombreFoto).toAbsolutePath();
  		Resource recurso = null;
  		try {
			recurso = new UrlResource(rutaFoto.toUri());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  		// VAMOS A VERIFICAR SI SE PUEDE LEER Y EXISTE
  		if(!recurso.exists() && !recurso.isReadable()) {
  			throw new RuntimeException("Error no se puede cargar la imagen: "+nombreFoto);
  		}
  		// VAMOS  DESCARGAR LA IMAGEN DESDE EL SERVIDOR AL CLIENTE
  		HttpHeaders cabecera = new HttpHeaders();
  		cabecera.add(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+ recurso.getFilename()+"\"");
  		
  		return new ResponseEntity<Resource>(recurso,HttpStatus.OK);
  	}
  	

}
