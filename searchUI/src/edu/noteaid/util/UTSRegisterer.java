package edu.noteaid.util;

import java.util.ArrayList;
import java.util.List;

import gov.nih.nlm.uts.webservice.content.AtomDTO;
import gov.nih.nlm.uts.webservice.content.ConceptDTO;
import gov.nih.nlm.uts.webservice.content.Psf;
import gov.nih.nlm.uts.webservice.content.SubsetDTO;
import gov.nih.nlm.uts.webservice.content.UtsWsContentController;
import gov.nih.nlm.uts.webservice.content.UtsWsContentControllerImplService;
import gov.nih.nlm.uts.webservice.security.UtsFault_Exception;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityControllerImplService;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityController;

public class UTSRegisterer {
	private String username = "uwmbiodlp";
	private String password = "Uwmpword2011";
	private String serviceName = "http://umlsks.nlm.nih.gov";
	
	public void getTicket() {
		UtsWsContentController utsContentService = (new UtsWsContentControllerImplService()).getUtsWsContentControllerImplPort();
		UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();
		String ticketGrantingTicket = "";
		
		try {
			ticketGrantingTicket = securityService.getProxyGrantTicket(username, password);
			 //use the Proxy Grant Ticket to get a Single Use Ticket
            String singleUseTicket1 = securityService.getProxyTicket(ticketGrantingTicket, serviceName);
            List<AtomDTO> myAtoms = new ArrayList<AtomDTO>();
            
            Psf myPsf = new Psf();
            myPsf.getIncludedSources().add("SNOMEDCT");
/*            myPsf.getIncludedTermTypes().add("FSN");
            myPsf.getIncludedTermTypes().add("SY");
            myPsf.getIncludedTermTypes().add("PT");
            myPsf.getIncludedTermTypes().add("OS");
            myPsf.getIncludedTermTypes().add("PR");
            myPsf.getIncludedTermTypes().add("DI");
*/            myAtoms = utsContentService.getConceptAtoms(singleUseTicket1, "2012AB", "C0085096", myPsf);

            for (int i = 0; i < myAtoms.size(); i++) {


            AtomDTO myAtomDTO = myAtoms.get(i);

            String aui = myAtomDTO.getUi();
            String source = myAtomDTO.getRootSource();
            String name = myAtomDTO.getTermString().getName();
            String TermType = myAtomDTO.getTermType();

            System.out.println(aui + "... " + name + "... " + TermType);
            }
            
            
		} catch (UtsFault_Exception e) {
			e.printStackTrace();
		} catch (gov.nih.nlm.uts.webservice.content.UtsFault_Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		UTSRegisterer reg = new UTSRegisterer();
		reg.getTicket();
	}

}
