package com.maknolja.web.controller;

import com.maknolja.exception.UserLoginException;
import com.maknolja.exception.UserRegisterException;
import com.maknolja.service.LocalFlightService;
import com.maknolja.service.LocalHotelService;
import com.maknolja.service.UserService;
import com.maknolja.vo.LocalFlight;
import com.maknolja.vo.LocalHotel;
import com.maknolja.vo.User;
import com.maknolja.web.utils.SessionUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.UUID;

@Controller
public class MainController {
	
	Logger logger = LogManager.getLogger(MainController.class);
	
	@Autowired
	LocalFlightService localFlightService;
	@Autowired
	LocalHotelService localHotelService;
	@Autowired
	UserService userService;
	@Autowired
	private JavaMailSender mailSender;
	
	@GetMapping(path = {"/", "/main"})
	public String main(Model model) {
		
		List<LocalFlight> startNames = localFlightService.getFlightsStartCityNames();
		List<LocalFlight> arriveNames = localFlightService.getFlightsArriveCityNames();
		List<LocalHotel> hotels = localHotelService.getAllLocalHotelsList();
		List<LocalHotel> names = localHotelService.getAllCityNames();

		model.addAttribute("startNames", startNames);
		model.addAttribute("arriveNames", arriveNames);
		model.addAttribute("hotels", hotels);
		model.addAttribute("names", names);

		return "main";
	}

	@ExceptionHandler(UserRegisterException.class)
	public String handleUserRegisterException(UserRegisterException ex, Model model) {
		model.addAttribute("error", ex.getMessage());
		return "user/login/form";
	}

	@ExceptionHandler(UserLoginException.class)
	public String handleLoginException(UserLoginException ex, Model model) {
		model.addAttribute("error", ex.getMessage());
		return "user/login/loginform";
	}

	@GetMapping("/register")
	public String registerform() {
		logger.debug("registerform() ?????????");
		logger.info("??????????????? ????????? ?????????");

		logger.debug("registerform() ?????????");
		return "user/login/form";
	}

	@PostMapping("/register")
	public String register(@RequestParam("id") String id, @RequestParam("password") String password ) {
		logger.debug("register() ?????????");

		User user = new User();
		user.setId(id);
		user.setPassword(password);

		userService.registerUser(user);

		logger.info("???????????? ?????? ????????? ?????????");
		logger.debug("register() ?????????");

		return "redirect:main";
	}

	@GetMapping("/login")
	public String loginform() {
		return "user/login/loginform";
	}

	@PostMapping("/login")
	public String login(@RequestParam("id") String userId, @RequestParam("password") String userPassword) {
		logger.debug("login() ?????????");
		logger.info("??????????????? ???????????? ?????????: " + userId);
		logger.info("??????????????? ???????????? ????????????: " + userPassword);

		userService.login(userId, userPassword);
		logger.debug("login() ?????????");

		// ????????? ??? ???????????? ???????????????
		String returnPath = (String) SessionUtils.getAttribute("returnPath");
			SessionUtils.removeAttribute("returnPath");
		if (returnPath != null) {
			return "redirect:" + returnPath;
		}
		return "redirect:main";
	}

	@GetMapping("/logout")
	public String logout() {
		SessionUtils.destroySession();
		return "redirect:main";
	}

	@PostMapping("/forgetPwd")
	public String forgetPwd (@RequestParam("id") String id){
		User user = userService.getUserById(id);
		if(user != null){
			String uuid = UUID.randomUUID().toString();
			uuid = uuid.substring(0,12);
			uuid = uuid.replace("-","");
			String tmpPwd = DigestUtils.sha256Hex(uuid);

			user.setPassword(tmpPwd);
			userService.updateUser(user);

			String subject = "MAKNOLJA ?????????????????? ??????";
			StringBuffer content = new StringBuffer();
			content.append("<html>");
			content.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
			content.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">");
			content.append("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" />");
			content.append("<style type=\"text/css\">");
			content.append("@media screen {@font-face {font-family: 'Lato';font-style: normal;font-weight: 700;src: local('Lato Bold'), local('Lato-Bold'), url(https:/*fonts.gstatic.com/s/lato/v11/qdgUG4U09HnJwhYI-uK18wLUuEpTyoUstqEm5AMlJo4.woff) format('woff');*/} @font-face {font-family: 'Lato';font-style: italic;font-weight: 400;src: local('Lato Italic'), local('Lato-Italic'), url(https:/*fonts.gstatic.com/s/lato/v11/RYyZNoeFgb0l7W3Vu1aSWOvvDin1pK8aKteLpeZ5c0A.woff) format('woff');*/} @font-face {font-family: 'Lato';font-style: italic;font-weight: 700;src: local('Lato Bold Italic'), local('Lato-BoldItalic'), url(https:/*fonts.gstatic.com/s/lato/v11/HkF_qI1x_noxlxhrhMQYELO3LdcAZYWl9Si6vvxL-qU.woff) format('woff');*/}}");
			content.append("body, table, td, a {-webkit-text-size-adjust: 100%; -ms-text-size-adjust: 100%;}");
			content.append("table, td {mso-table-lspace: 0pt;mso-table-rspace: 0pt;}");
			content.append("img {-ms-interpolation-mode: bicubic;}");
			content.append("img {border: 0;height: auto;line-height: 100%; outline: none;text-decoration: none;}");
			content.append("table {border-collapse: collapse !important;}");
			content.append("body {height: 100% !important;margin: 0 !important;padding: 0 !important;width: 100% !important;}");
			content.append("a[x-apple-data-detectors] {color: inherit !important;text-decoration: none !important;font-size: inherit !important;font-family: inherit !important;font-weight: inherit !important;line-height: inherit !important;}");
			content.append("@media screen and (max-width:600px) {h1 {font-size: 32px !important;line-height: 32px !important;}}");
			content.append("div[style*=\"margin: 16px 0;\"] {margin: 0 !important;}");
			content.append("</style>");
			content.append("</head>");
			content.append("<body style=\"background-color: #f4f4f4; margin: 0 !important; padding: 0 !important;\">");
			content.append("<div style=\"display: none; font-size: 1px; color: #fefefe; line-height: 1px; font-family: 'Lato', Helvetica, Arial, sans-serif; max-height: 0px; max-width: 0px; opacity: 0; overflow: hidden;\"> We're thrilled to have you here! Get ready to dive into your new account. </div>");
			content.append("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">");
			content.append("<tr><td bgcolor=\"#FFA73B\" align=\"center\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\"><tr><td align=\"center\" valign=\"top\" style=\"padding: 40px 10px 40px 10px;\"> </td></tr></table></td></tr>");
			content.append("<tr><td bgcolor=\"#FFA73B\" align=\"center\" style=\"padding: 0px 10px 0px 10px;\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\"><tr><td bgcolor=\"#ffffff\" align=\"center\" valign=\"top\" style=\"padding: 40px 20px 20px 20px; border-radius: 4px 4px 0px 0px; color: #111111; font-family: 'Lato', Helvetica, Arial, sans-serif; font-size: 48px; font-weight: 400; letter-spacing: 4px; line-height: 48px;\"><h1 style=\"font-size: 48px; font-weight: 400; margin: 2;\">????????????????????????</h1> <img src=\" https://img.icons8.com/clouds/100/000000/handshake.png\" width=\"125\" height=\"120\" style=\"display: block; border: 0px;\" /></td></tr></table></td></tr>");
			content.append("<tr><td bgcolor=\"#f4f4f4\" align=\"center\" style=\"padding: 0px 10px 0px 10px;\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\"><tr><td bgcolor=\"#ffffff\" align=\"left\" style=\"padding: 20px 30px 40px 30px; color: #666666; font-family: 'Lato', Helvetica, Arial, sans-serif; font-size: 18px; font-weight: 400; line-height: 25px;\"><p style=\"margin: 0;\">?????? ?????????????????? ????????? ????????? ???????????? ????????? ????????? ?????????.</p> </td> </tr>");
			content.append("<tr><td bgcolor=\"#ffffff\" align=\"left\" style=\"padding: 0px 30px 0px 30px; color: #666666; font-family: 'Lato', Helvetica, Arial, sans-serif; font-size: 18px; font-weight: 400; line-height: 25px;\"><p style=\"margin: 0; padding:15px;\">?????????????????? : <strong>" + uuid + "</strong></p>");
			content.append("</td> </tr><tr> <td bgcolor=\"#ffffff\" align=\"left\" style=\"padding: 20px 30px 20px 30px; color: #666666; font-family: 'Lato', Helvetica, Arial, sans-serif; font-size: 18px; font-weight: 400; line-height: 25px;\"></td> </tr> <tr> <td bgcolor=\"#ffffff\" align=\"left\" style=\"padding: 0px 30px 20px 30px; color: #666666; font-family: 'Lato', Helvetica, Arial, sans-serif; font-size: 18px; font-weight: 400; line-height: 25px;\"> <p style=\"margin: 0;\">????????????????????? ???????????? ??? ????????? ?????? ??????????????????.</p></td></tr><tr><td bgcolor=\"#ffffff\" align=\"left\" style=\"padding: 0px 30px 40px 30px; border-radius: 0px 0px 4px 4px; color: #666666; font-family: 'Lato', Helvetica, Arial, sans-serif; font-size: 18px; font-weight: 400; line-height: 25px;\"><p style=\"margin: 0;\">Cheers,<br>MAKNOLJA Team</p></td></tr></table></td></tr>");
			content.append("<tr><td bgcolor=\"#f4f4f4\" align=\"center\" style=\"padding: 30px 10px 0px 10px;\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\"><tr><td bgcolor=\"#FFECD1\" align=\"center\" style=\"padding: 30px 30px 30px 30px; border-radius: 4px 4px 4px 4px; color: #666666; font-family: 'Lato', Helvetica, Arial, sans-serif; font-size: 18px; font-weight: 400; line-height: 25px;\"><h2 style=\"font-size: 20px; font-weight: 400; color: #111111; margin: 0;\">????????? ????????????????????</h2><p style=\"margin: 0;\"><a href=\"http://localhost:8080\" target=\"_blank\" style=\"color: #FFA73B;\">MAKNOLJA.COM??? ?????????????????? !</a></p></td></tr></table></td></tr>");
			content.append("<tr><td bgcolor=\"#f4f4f4\" align=\"center\" style=\"padding: 0px 10px 0px 10px;\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\"><tr><td bgcolor=\"#f4f4f4\" align=\"left\" style=\"padding: 0px 30px 30px 30px; color: #666666; font-family: 'Lato', Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 400; line-height: 18px;\"> <br></td></tr></table></td></tr></table></body></html>");

			String from = "eknnnooosw@gmail.com";
			String to = id;

			try {
				MimeMessage mail = mailSender.createMimeMessage();
				MimeMessageHelper mailHelper = new MimeMessageHelper(mail, true, "UTF-8");
				// true??? ???????????? ???????????? ?????????????????? ??????

				/*
				 * ????????? ????????? ???????????? ???????????? ????????? ????????? ?????? ??????
				 * MimeMessageHelper mailHelper = new MimeMessageHelper(mail,"UTF-8");
				 */

				mailHelper.setFrom(from);
				// ?????? ????????? ????????? ?????? ????????? smtp ????????? ?????? ?????? ?????? ????????? ????????????(setFrom())????????? ??????
				// ??????????????? ??????????????? ?????????????????? ?????? ?????? ?????? ?????? ??????????????? ????????? ????????? ??????????????? ?????????.
				//mailHelper.setFrom("???????????? ?????? <???????????? ?????????@???????????????>");
				mailHelper.setTo(to);
				mailHelper.setSubject(subject);
				mailHelper.setText(content.toString(), true);
				// true??? html??? ?????????????????? ???????????????.

				mailSender.send(mail);

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return "redirect:main";
	}
	
	@GetMapping("/mypage")
	public String mypage() {
		return "user/member/mypage";
	}
	@GetMapping("/profile")
	public String profile() {
		return "user/member/profile";
	}
	@GetMapping("/accountpassword")
	public String acccountpassword() {
		return "user/member/accountpassword";
	}
	
}
