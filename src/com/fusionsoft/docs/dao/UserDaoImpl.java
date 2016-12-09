package com.fusionsoft.docs.dao;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projections;
import org.hibernate.query.Query;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fusionsoft.docs.model.Applicant;
import com.fusionsoft.docs.model.Certification;
import com.fusionsoft.docs.model.Contact;
import com.fusionsoft.docs.model.CustomRole;
import com.fusionsoft.docs.model.CustomUser;
import com.fusionsoft.docs.model.Document;
import com.fusionsoft.docs.model.Education;
import com.fusionsoft.docs.model.Experience;
import com.fusionsoft.docs.model.Immigration;
import com.fusionsoft.docs.model.Passport;
import com.fusionsoft.docs.model.Profile;
import com.fusionsoft.docs.model.Travel;
import com.fusionsoft.docs.service.UserService;

@Repository
@Transactional
public class UserDaoImpl implements UserDao {

	@Autowired
	private SessionFactory sessionFactory;
	@Autowired
	private UserService userservice;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@SuppressWarnings("unused")
	private Session openSession() {
		return sessionFactory.getCurrentSession();
	}

	public CustomUser findUserByUsername(final String username) {

		System.out.println(" In DAO ");

		List<CustomUser> users = new ArrayList<CustomUser>();

		users = getSessionFactory().openSession().createQuery("from CustomUser where username=?")
				.setParameter(0, username).list();

		if (users.size() != 1) {
			return null;
		}

		CustomUser user = users.get(0);

		List<CustomRole> roles = new ArrayList<CustomRole>();

		if (user.getUserrole() == 1) {

			CustomRole r1 = new CustomRole();
			r1.setName("ROLE_ADMIN");
			roles.add(r1);

			user.setAuthorities(roles);
		} else if (user.getUserrole() == 2) {
			CustomRole r1 = new CustomRole();
			r1.setName("ROLE_USER");
			roles.add(r1);

			user.setAuthorities(roles);
		}

		return user;

	}
	public List<Profile> findalluserprofiles() {
		List<Profile> profiles = new ArrayList<Profile>();
		Criteria cr = getSessionFactory().openSession().createCriteria(Profile.class)
				.setProjection(Projections.projectionList().add(Projections.property("firstname"), "firstname")
						.add(Projections.property("lastname"), "lastname").add(Projections.property("email"), "email")
						.add(Projections.property("phone"), "phone").add(Projections.property("userid"), "userid"))
				.setResultTransformer(Transformers.aliasToBean(Profile.class));
		profiles = cr.list();
		
		return profiles;
	}
	@Override
	public Profile findprofile(int userid) {
		// TODO Auto-generated method stub
		Session session = getSessionFactory().openSession().getSession();
		session.beginTransaction();
		Profile profile = session.get(Profile.class, userid);
		System.out.println("The required Name is" + profile.getLastname());
		session.getTransaction().commit();
		session.close();
		return profile;
	}

	@Override
	public int updateprofile(Profile updateuser, int userid) {
         int id;
         System.out.println("The user name is "+updateuser.getFirstname());
		Session session = getSessionFactory().openSession().getSession();
		session.beginTransaction();
		Query query = session.createQuery(
				"update Profile set firstname = :firstname , lastname = :lastname, email = :email, phone = :phone, zip = :zip, country = :country, "
						+ "adminnotes = :adminnotes, address = :address" + " where id = :userid");
		query.setParameter("firstname", updateuser.getFirstname());
		query.setParameter("lastname", updateuser.getLastname());
		query.setParameter("email", updateuser.getEmail());
		query.setParameter("phone", updateuser.getPhone());
		query.setParameter("zip", updateuser.getZip());
		query.setParameter("country", updateuser.getCountry());
		query.setParameter("adminnotes", updateuser.getAdminnotes());
		query.setParameter("address", updateuser.getAddress());
		query.setParameter("userid", userid);
		id = query.executeUpdate();
		session.getTransaction().commit();
		session.close();

		return id;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public int deleteprofile(int userid) {
		// TODO Auto-generated method stub
		@SuppressWarnings("deprecation")
		Session session = getSessionFactory().openSession().getSession();
		session.beginTransaction();
		Serializable id = new Integer(userid);
		Object persistentInstance = session.load(CustomUser.class, id);
		if (persistentInstance != null) {
		    session.delete(persistentInstance);
		}
		
		session.getTransaction().commit();
		return 0;
	}

	@Override
	public Document finddocument(int docid) {
		// TODO Auto-generated method stub
		Session session = getSessionFactory().openSession().getSession();
		session.beginTransaction();
		Document document = session.get(Document.class, docid);
		session.getTransaction().commit();
		session.close();
		return document;
	}
    @SuppressWarnings("deprecation")
	public List<Document> findparticulardocuments(int userid,String doctitle){
    	List<Document> particulardocuments = new ArrayList<Document>();
    	Session session = sessionFactory.openSession();
    	Query query = session.createQuery("FROM Document Where userid = ? and doctype = ?");
        query.setParameter(0, userid);
        query.setParameter(1, doctitle);
    	particulardocuments = query.list();
    	session.close();
    	System.out.println("The list in DAO is "+particulardocuments.size());
    	return particulardocuments;
    	}
    public int savedocument(Document document){
        int userid;
    	Session session = sessionFactory.openSession().getSession();
    	session.beginTransaction();
        userid = (int) session.save(document);
    	session.getTransaction().commit();
    	session.close();
    	return userid;
    }

	@Override
	public CustomUser findCustomUser(int userid) {
		Session session = sessionFactory.openSession().getSession();
		CustomUser customuser = session.get(CustomUser.class, userid);
		session.close();
		return customuser;
	}

	@Override
	public void deletedoc(int docid) {
		Session session = sessionFactory.openSession().getSession();
		session.beginTransaction();
		Serializable id = new Integer(docid);
		Object persistentInstance = session.load(Document.class, id);
		if (persistentInstance != null) {
		    session.delete(persistentInstance);
		}
		session.getTransaction().commit();
		session.close();
	}

	@Override
	public int finduseridbydocid(int docid) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession().getSession();
		session.beginTransaction();
		Query query = session.createSQLQuery("SELECT userid FROM documents WHERE docid =?");
		query.setParameter(0, docid);
		int userid = (int) query.list().get(0);
		session.getTransaction().commit();
		session.close();
		
		System.out.println("The userid in the dao layer is "+userid);
		return userid;
	}

	@Override
	public int saveProfile(CustomUser customuser) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession().getSession();
		session.beginTransaction();
		int userid = (int) session.save(customuser);
		session.getTransaction().commit();
		session.close();
		return userid;
	}

	@Override
	public int saveexperience(Experience experience) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession().getSession();
		session.beginTransaction();
		int userid = (int) session.save(experience);
		session.getTransaction().commit();
		session.close();
		return userid;
	}

	@Override
	public int saveeducation(Education education) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession().getSession();
		int userid = (int) session.save(education);
		session.close();
		return userid;
	}

	@Override
	public Immigration findimmigration(int id) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession().getSession();
		Immigration immigration = session.get(Immigration.class, id);
		session.close();
		return immigration;
	}

	@Override
	public void saveimmigration(Immigration immigration) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession().getSession();
		session.beginTransaction();
		System.out.println("The saving happeing in DAO lAYER");
		session.save(immigration);
		session.getTransaction().commit();
		session.close();
		
	}

	@Override
	public List<Experience> findexperiences(int id) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
    	Query query = session.createQuery("FROM Experience Where userid = ?");
    	query.setParameter(0, id);
    	List<Experience> experiences = query.list();
    	session.close();
		return experiences;
	}

	@Override
	public List<Education> findqualifications(int id) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
    	Query query = session.createQuery("FROM Education Where userid = ?");
    	query.setParameter(0, id);
    	List<Education> educationqualifications = query.list();
    	System.out.println("In Dao The List Size is"+educationqualifications.size());
    	session.close();
		return educationqualifications;
	}

	@Override
	public void deleteexperience(int expid) {
		Session session = sessionFactory.openSession().getSession();
		System.out.println("In DaO lAYER I AM pRESENT");
		Serializable id = new Integer(expid);
		session.beginTransaction();
		Object persistentInstance = session.load(Experience.class, id);
		if (persistentInstance != null) {
		    session.delete(persistentInstance);
		}
		session.getTransaction().commit();
		session.close();
		
		
	}

	@Override
	public void updateimmigration(Immigration immigration) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession().getSession();
		Immigration immigrationload = session.get(Immigration.class, immigration.getUserid());
		immigrationload.setCurrentstatus(immigration.getCurrentstatus());
		immigrationload.setExpirydate(immigration.getExpirydate());
		immigrationload.setI94number(immigration.getI94number());
		immigrationload.setPassportnumber(immigration.getPassportnumber());
		immigrationload.setPlaceofissuance(immigration.getPlaceofissuance());
		immigrationload.setLastentrydate(immigration.getLastentrydate());
		immigrationload.setIssueddate(immigration.getIssueddate());
		immigrationload.setStatusexpiry(immigration.getStatusexpiry());
		session.beginTransaction();
		session.saveOrUpdate(immigrationload);
		session.getTransaction().commit();;
		session.close();
		
		
	}

	@Override
	public void deleteeducation(int eduid) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession().getSession();

		Serializable id = new Integer(eduid);
		session.beginTransaction();
		Object persistentInstance = session.load(Education.class, id);
		if (persistentInstance != null) {
		    session.delete(persistentInstance);
		}
		session.getTransaction().commit();
		session.close();
	}

	@Override
	public void updatefirstlogin(int id) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession().getSession();
		Serializable userid = new Integer(id);
		session.beginTransaction();
	    CustomUser user = session.get(CustomUser.class, userid);
	    user.setFirstlogin(0);
	    session.save(user);
	    session.getTransaction().commit();
	    session.close();
		
	}

	@Override
	public Applicant findapplicant(int id) {
		// Getting The Applicant Details From The DataBase
		Applicant applicant;
		Session session = sessionFactory.openSession();
        session.getTransaction().begin();
		@SuppressWarnings("rawtypes")
		TypedQuery query = session.createQuery("from Applicant where userid = :userid ");
		query.setParameter("userid", id);
		if(query.getResultList().size() == 0)
		{
			applicant = new Applicant();
		}
		else{
		   applicant = (Applicant) query.getResultList().get(0);
		}
		session.getTransaction().commit();
		session.close();
		return applicant;
	}

	@Override
	/*Saving A new Application as User enters The Application Information For The First Time*/
	public void saveapplication(Applicant applicant) {
		Session session = getSessionFactory().openSession();
		session.beginTransaction();
		session.save(applicant);
		session.getTransaction().commit();
		session.close();
	}

	@Override
	/*Updating The Application as User Makes Changes to Aplication*/
	public void updateapplication(Applicant applicant) {
		// TODO Auto-generated method stub
		Session session = getSessionFactory().openSession();
		Applicant updatedapplicant = session.get(Applicant.class, applicant.getUserid());
		session.beginTransaction();
		updatedapplicant.setAdminnotes(applicant.getAdminnotes());
		updatedapplicant.setAliennumber(applicant.getAliennumber());
		updatedapplicant.setApplicationtype(applicant.getApplicationtype());
		updatedapplicant.setCitizenship(applicant.getCitizenship());
		updatedapplicant.setCountryofbirth(applicant.getCountryofbirth());
		updatedapplicant.setCurrentemployerpetitionnumber(applicant.getCurrentemployerpetitionnumber());
		updatedapplicant.setCurrentvisaexpirydate(applicant.getCurrentvisaexpirydate());
		updatedapplicant.setDateofbirth(applicant.getDateofbirth());
		updatedapplicant.setEadvalidupto(applicant.getEadvalidupto());
		updatedapplicant.setFirstname(applicant.getFirstname());
		updatedapplicant.setFullname(applicant.getFullname());
		updatedapplicant.setLastentrydatetous(applicant.getLastentrydatetous());
		updatedapplicant.setLastname(applicant.getLastname());
		updatedapplicant.setLatesti94number(applicant.getLatesti94number());
		updatedapplicant.setMaidenname(applicant.getMaidenname());
		updatedapplicant.setMiddlename(applicant.getMiddlename());
		updatedapplicant.setPrefix(applicant.getPrefix());
		updatedapplicant.setProvinceofbirth(applicant.getProvinceofbirth());
		updatedapplicant.setSocialsecuritynumber(applicant.getSocialsecuritynumber());
		updatedapplicant.setStatus(applicant.getStatus());
		updatedapplicant.setUsernotes(applicant.getUsernotes());
		updatedapplicant.setUsvisit(applicant.getUsvisit());
		session.saveOrUpdate(updatedapplicant);
		session.getTransaction().commit();
		session.close();
	}

	@Override
	public Contact findcontact(int userid) {
		// finding contact details
		Session session = getSessionFactory().openSession();
		Contact contact =  new Contact();
		try{
			session.beginTransaction();
			contact = session.get(Contact.class, userid);
			session.getTransaction().commit();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		finally{
			session.close();
		}
		return contact;
	}

	@Override
	public void savecontact(Contact contact) {
		// saving contact details
				Session session = getSessionFactory().openSession();
				try{
					session.beginTransaction();
					session.save(contact);
					session.getTransaction().commit();
				}catch(Exception e){
					e.printStackTrace();
				}
				
				finally{
					session.close();
				}
			}

	@Override
	public void updatecontact(Contact contact) {
		// updating contact details
				Session session = getSessionFactory().openSession();
				try{
					session.beginTransaction();
					Contact updatecontact = session.get(Contact.class, contact.getUserid());
					updatecontact.setAddress1(contact.getAddress1());
					updatecontact.setAddress2(contact.getAddress2());
					updatecontact.setAddress3(contact.getAddress3());
					updatecontact.setCity(contact.getCity());
					updatecontact.setCity2(contact.getCity2());
					updatecontact.setCity3(contact.getCity3());
					updatecontact.setCountry(contact.getCountry());
					updatecontact.setCountry2(contact.getCountry2());
					updatecontact.setCountry3(contact.getCountry3());
					updatecontact.setEmail(contact.getEmail());
					updatecontact.setHomephonenumber(contact.getHomephonenumber());
					updatecontact.setPhonenumber(contact.getPhonenumber());
					updatecontact.setState(contact.getState());
					updatecontact.setState2(contact.getState2());
					updatecontact.setState3(contact.getState3());
					updatecontact.setZipcode(contact.getZipcode());
					updatecontact.setZipcode2(contact.getZipcode2());
					updatecontact.setZipcode3(contact.getZipcode3());
					session.save(updatecontact);
					session.getTransaction().commit();
				}catch(Exception e){
					e.printStackTrace();
				}
				
				finally{
					session.close();
				}
			}

	@Override
	public Passport findpassport(int userid) {
		// finding passport in the database
		Session session = getSessionFactory().openSession();
		Passport passport = new Passport();
		try{
			session.beginTransaction();
			passport = session.get(Passport.class, userid);
			session.getTransaction().commit();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		finally{
			session.close();
		}
		return passport;
	}

	@Override
	public void savepassport(Passport passport) {
		// saving passport details
		Session session = getSessionFactory().openSession();
		try{
			session.beginTransaction();
			session.save(passport);
			session.getTransaction().commit();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		finally{
			session.close();
		}
	}

	@Override
	public void updatepassport(Passport passport) {
		// TODO Auto-generated method stub
		Session session = getSessionFactory().openSession();
		try{
			Transaction tx = session.beginTransaction();
			Passport updatedpassport = session.get(Passport.class, passport.getUserid());
			updatedpassport.setLatestpassportnumber(passport.getLatestpassportnumber());
			updatedpassport.setPassportexpirydate(passport.getPassportexpirydate());
			updatedpassport.setPassportissuancedate(passport.getPassportissuancedate());
			updatedpassport.setPassportissuedlocation(passport.getPassportissuedlocation());
			session.save(updatedpassport);
			tx.commit();
	}catch(Exception e){
		session.getTransaction().rollback();
		e.printStackTrace();
	}
		finally{
			session.close();
		}
	}

	@Override
	public List<Travel> findtraveldetails(int userid) {
		// pulling up the list of travel details to be shown in the view
		List<Travel> traveldetails = new ArrayList<Travel>();
		Session session = sessionFactory.openSession();
		try{
        session.getTransaction().begin();
		@SuppressWarnings("unchecked")
		TypedQuery<Travel> query = session.createQuery("from Travel where userid = :userid ");
		query.setParameter("userid", userid);
		traveldetails = query.getResultList();
		session.getTransaction().commit();
		}catch(Exception e){
			session.getTransaction().rollback();
			e.printStackTrace();
		}
		finally{
			session.close();
		}
		return traveldetails;
	}

	@Override
	public Travel findtravel(int travelid) {
		// TODO Auto-generated method stub
		Session session = getSessionFactory().openSession();
		Travel travel = new Travel();
		try{
			session.beginTransaction();
			travel = session.get(Travel.class, travelid);
			session.getTransaction().commit();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		finally{
			session.close();
		}
		return travel;
	}

	@Override
	public void savetravel(Travel travel) {
		// TODO Auto-generated method stub
		Session session = getSessionFactory().openSession();
		try{
			session.beginTransaction();
			session.save(travel);
			session.getTransaction().commit();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		finally{
			session.close();
		}
	}

	@Override
	public void updatetravel(Travel travel) {
		// TODO Auto-generated method stub
		Session session = getSessionFactory().openSession();
		try{
			session.beginTransaction();
			Travel updatedtravel = session.get(Travel.class, travel.getTravelid());
			updatedtravel.setEntrydatetous(travel.getEntrydatetous());
			updatedtravel.setExitdatefromus(travel.getExitdatefromus());
			updatedtravel.setPortofentry(travel.getPortofentry());
			updatedtravel.setStatusattimeofentry(travel.getStatusattimeofentry());
			updatedtravel.setStatusvalidfrom(travel.getStatusvalidfrom());
			updatedtravel.setStatusvalidtill(travel.getStatusvalidtill());
			updatedtravel.setTotalmonthsoutsideus(travel.getTotalmonthsoutsideus());
			session.getTransaction().commit();
	}catch(Exception e){
		session.getTransaction().rollback();
		e.printStackTrace();
	}
		finally{
			session.close();
		}
	}
	
	@Override
	public List<Certification> findcertificationdetails(int userid) {
		// pulling up the list of travel details to be shown in the view
		List<Certification> certificationdetails = new ArrayList<Certification>();
		Session session = sessionFactory.openSession();
		try{
        session.getTransaction().begin();
		@SuppressWarnings("unchecked")
		TypedQuery<Certification> query = session.createQuery("from Certification where userid = :userid ");
		query.setParameter("userid", userid);
		certificationdetails = query.getResultList();
		session.getTransaction().commit();
		}catch(Exception e){
			session.getTransaction().rollback();
			e.printStackTrace();
		}
		finally{
			session.close();
		}
		return certificationdetails;
	}

	@Override
	public void updateeducation(Education education) {
		// TODO Auto-generated method stub
		Session session = getSessionFactory().openSession();
		try{
			session.beginTransaction();
			Education updateeducation = session.get(Education.class, education.getEduid());
			updateeducation.setAddress(education.getAddress());
			updateeducation.setCity(education.getCity());
			updateeducation.setCountry(education.getCountry());
			updateeducation.setCourse(education.getCourse());
			updateeducation.setGpa(education.getGrade());
			updateeducation.setLevelofeducation(education.getLevelofeducation());
			updateeducation.setLocation(education.getLocation());
			updateeducation.setMonthofpassing(education.getMonthofpassing());
			updateeducation.setState(education.getState());
			updateeducation.setUniversity(education.getUniversity());
			updateeducation.setUssevisnumber(education.getUssevisnumber());
			updateeducation.setYearofpassing(education.getYearofpassing());
			updateeducation.setZipcode(education.getZipcode());
			session.save(updateeducation);
			session.getTransaction().commit();
		}catch(Exception e){
			session.getTransaction().rollback();
			e.printStackTrace();
		}
		finally{
			session.close();
		}
		
	}

	@Override
	public void updateexperience(Experience experience) {
		// TODO Auto-generated method stub
		Session session = getSessionFactory().openSession();
		try{
			session.beginTransaction();
			Experience updatedexperience = session.get(Experience.class, experience.getExpid());
			updatedexperience.setCountry(experience.getCountry());
			updatedexperience.setdesignation(experience.getdesignation());
			updatedexperience.setEmployer(experience.getEmployer());
			updatedexperience.setEnddate(experience.getEnddate());
			updatedexperience.setJoineddate(experience.getJoineddate());
			updatedexperience.setState(experience.getState());
			updatedexperience.setTotalmonthsworked(experience.getTotalmonthsworked());
			session.save(updatedexperience);
			session.getTransaction().commit();
		}catch(Exception e){
			session.getTransaction().rollback();
			e.printStackTrace();
		}
		finally{
			session.close();
		}
	}

	@Override
	public void savecertification(Certification certification) {
		// TODO Auto-generated method stubSession session = getSessionFactory().openSession();
		Session session = getSessionFactory().openSession();
		try{
			session.beginTransaction();
			session.save(certification);
			session.getTransaction().commit();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		finally{
			session.close();
		}
		
		
		
	}

	@Override
	public void updatecertification(Certification certification) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
				Session session = getSessionFactory().openSession();
				try{
					session.beginTransaction();
					Certification updatedcertification = session.get(Certification.class,certification.getCertificationId());
					updatedcertification.setAddress(certification.getAddress());
					updatedcertification.setCertificationName(certification.getCertificationName());
					updatedcertification.setCity(certification.getCity());
					updatedcertification.setCountry(certification.getCountry());
					updatedcertification.setMonthOfPassing(certification.getMonthOfPassing());
					updatedcertification.setOrganisation(certification.getOrganisation());
					updatedcertification.setSpecialisation(certification.getSpecialisation());
					updatedcertification.setState(certification.getState());
					updatedcertification.setYearOfPassing(certification.getYearOfPassing());
					updatedcertification.setZipcode(certification.getZipcode());
					session.getTransaction().commit();
			}catch(Exception e){
				session.getTransaction().rollback();
				e.printStackTrace();
			}
				finally{
					session.close();
				}
		
	}

	@Override
	public Experience findexperience(int expid) {
		// TODO Auto-generated method stub
		Session session = getSessionFactory().openSession();
		Experience experience = new Experience();
		try{
			session.beginTransaction();
			experience = session.get(Experience.class, expid);
			session.getTransaction().commit();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		finally{
			session.close();
		}
		return experience;
		
	}
}
		
